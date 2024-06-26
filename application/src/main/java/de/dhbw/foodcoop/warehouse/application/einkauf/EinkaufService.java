package de.dhbw.foodcoop.warehouse.application.einkauf;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.dhbw.foodcoop.warehouse.application.admin.ConfigurationService;
import de.dhbw.foodcoop.warehouse.application.bestellungsliste.BestellÜbersichtService;
import de.dhbw.foodcoop.warehouse.application.deadline.DeadlineService;
import de.dhbw.foodcoop.warehouse.application.diskrepanz.DiscrepancyService;
import de.dhbw.foodcoop.warehouse.application.frischbestellung.FrischBestellungService;
import de.dhbw.foodcoop.warehouse.domain.entities.BestandBuyEntity;
import de.dhbw.foodcoop.warehouse.domain.entities.BestellUebersicht;
import de.dhbw.foodcoop.warehouse.domain.entities.BestellungBuyEntity;
import de.dhbw.foodcoop.warehouse.domain.entities.BrotBestellung;
import de.dhbw.foodcoop.warehouse.domain.entities.Deadline;
import de.dhbw.foodcoop.warehouse.domain.entities.DiscrepancyEntity;
import de.dhbw.foodcoop.warehouse.domain.entities.EinkaufEntity;
import de.dhbw.foodcoop.warehouse.domain.entities.FrischBestand;
import de.dhbw.foodcoop.warehouse.domain.entities.FrischBestellung;
import de.dhbw.foodcoop.warehouse.domain.entities.Kategorie;
import de.dhbw.foodcoop.warehouse.domain.entities.Produkt;
import de.dhbw.foodcoop.warehouse.domain.entities.TooMuchBuyEntity;
import de.dhbw.foodcoop.warehouse.domain.repositories.EinkaufRepository;

@Service
public class EinkaufService {


	
	@Autowired
    private EinkaufRepository einkaufRepository;
	
	@Autowired
    private BestellÜbersichtService service;
	@Autowired
    private FrischBestellungService frischService;
	
	@Autowired
	private DeadlineService deadlineService;
	
	@Autowired
	private DiscrepancyService discrepancyService;
	
	@Autowired
	private ConfigurationService configService;

    public BestandBuyEntity createBestandBuyEntityForPersonOrder(Produkt bestand, double amount) {
    	BestandBuyEntity bbe = new BestandBuyEntity();
    	bbe.setId(UUID.randomUUID().toString());
    	bbe.setAmount(amount);
    	bbe.setBestand(bestand);
    	return bbe;
    }
    

    public EinkaufEntity einkaufDurchführen( String personId, List<BestellungBuyEntity> vergleiche, List<BestandBuyEntity> bestandBuy, List<TooMuchBuyEntity> tooMuchBuy) throws Exception {
    	
        EinkaufEntity einkauf = new EinkaufEntity();
        einkauf.setId(UUID.randomUUID().toString());
        einkauf.setPersonId(personId);

        einkauf.setDate(LocalDateTime.now());
        einkauf = einkaufRepository.speichern(einkauf);
        if(bestandBuy != null) {
	        for(BestandBuyEntity bbe : bestandBuy) {
	        	//bbe.setEinkauf(einkauf);
	        //	bestandBuyRepository.speichern(bbe);
	            if(einkauf.getBestandEinkauf() == null) {
	            	einkauf.setBestandEinkauf(new ArrayList<BestandBuyEntity>());
	            }
	            	if(bbe.getBestand().getLagerbestand().getIstLagerbestand() - bbe.getAmount() < 0) {
	            		einkaufRepository.deleteById(einkauf.getId());
	            		throw new Exception("Insufficient Lagerbestand!");
	            	}
	            	bbe.getBestand().getLagerbestand().setIstLagerbestand(bbe.getBestand().getLagerbestand().getIstLagerbestand() - bbe.getAmount());
	        		einkauf.getBestandEinkauf().add(bbe);
	        }
        }
        if(vergleiche != null) {
        	for (BestellungBuyEntity ebv : vergleiche) {
        		
            	// Füge den Vergleich dem Einkauf hinzu
            	if(einkauf.getBestellungsEinkauf() == null) {
            		einkauf.setBestellungsEinkauf(new ArrayList<BestellungBuyEntity>());
            	}
           
            		einkauf.getBestellungsEinkauf().add(ebv);
        	}
        }
        
        if(tooMuchBuy != null) {
        	for(TooMuchBuyEntity e: tooMuchBuy) {
        		if(einkauf.getTooMuchEinkauf() == null) {
        			einkauf.setTooMuchEinkauf(new ArrayList<TooMuchBuyEntity>());
        		}
        		e.getDiscrepancy().setZuVielzuWenig(e.getDiscrepancy().getZuVielzuWenig() - (float)e.getAmount());
        		einkauf.getTooMuchEinkauf().add(e);
        	}
        }
        

        
        einkauf.setTooMuchPriceAtTime(calculatePriceForTooMuch(einkauf));
        einkauf.setBestandPriceAtTime(calculatePriceForBestandBuy(einkauf));
        einkauf.setBreadPriceAtTime(calculatePriceForBread(einkauf));
        einkauf.setFreshPriceAtTime(calculatePriceForFresh(einkauf));
        einkauf.setDeliveryCostAtTime(calculateDeliveryCostForShopping(einkauf));
        
        
        
        BestellUebersicht be = service.getLastUebersicht();
        if(be != null) {
        	List<DiscrepancyEntity> discrepancies = be.getDiscrepancy();
        	List<BestellungBuyEntity> bestellungen = einkauf.getBestellungsEinkauf();
        			
        	Optional<Deadline> date1 = deadlineService.getByPosition(0);
        	
        	if(date1.isPresent()) {
        	
        	
        	LocalDateTime datum1 = date1.get().getDatum();
         //	List<FrischBestellung> frischBestellungen = frischService.findByDateBetween(datum1, datum2, personId);
         	String eID = einkauf.getId();
         	List<EinkaufEntity> einkaufeFromPerson = einkaufRepository.alleAktuellenVonPerson(datum1, personId).stream().filter(t -> t.getId() != eID).collect(Collectors.toList());
         	
         	// Rechnet zusammen wie viel eine Person von einem Produkt genommen hat
         	
         	HashMap<FrischBestand, Double> mapAmountForOrder = new HashMap<>();
         	 if(vergleiche != null) {
         		for (BestellungBuyEntity ebv : vergleiche) {
         			if(ebv.getBestellung() != null && ebv.getBestellung() instanceof FrischBestellung)  {
         				if(((FrischBestellung) ebv.getBestellung()).getFrischbestand().isSpezialfallBestelleinheit()) {
         					mapAmountForOrder.merge(((FrischBestellung) ebv.getBestellung()).getFrischbestand(), ebv.getBestellung().getBestellmenge(), Double::sum);
         				} else {
         					mapAmountForOrder.merge(((FrischBestellung) ebv.getBestellung()).getFrischbestand(), ebv.getAmount(), Double::sum);
         				}
         			}
         		}
         		//Person kann mehrere Einkäufe durchgeführt haben innerhalb einer Woche (und dabei das gleiche Produkt mehrfach gekauft haben)
         		einkaufeFromPerson.forEach(t -> {
         			if(t.getBestellungsEinkauf() != null) {
         				for(BestellungBuyEntity eb : t.getBestellungsEinkauf()) {
         					if(eb.getBestellung() != null && eb.getBestellung() instanceof FrischBestellung) {
         						if(((FrischBestellung) eb.getBestellung()).getFrischbestand().isSpezialfallBestelleinheit()) {
                 					mapAmountForOrder.merge(((FrischBestellung) eb.getBestellung()).getFrischbestand(), eb.getBestellung().getBestellmenge(), Double::sum);
                 				} else {
                 					mapAmountForOrder.merge(((FrischBestellung) eb.getBestellung()).getFrischbestand(), eb.getAmount(), Double::sum);
                 				}
         					}
         				}
         			}
         		});
         	 }
         	
         	
         	 
         	 //Ab hier automatische Anpassung von zu viel zu wenig
         	 if(bestellungen != null) {
         	Set<Kategorie> katSet = new HashSet<>();
         	bestellungen.forEach(t -> {
         		if(t.getBestellung() instanceof FrischBestellung) {
         			FrischBestellung f = (FrischBestellung) t.getBestellung();
//         			if(f.getFrischbestand().getKategorie().isMixable()) {
//         				katSet.add(f.getFrischbestand().getKategorie());
//         			} else  {
         				double sumOrderedFromPerson =  (Math.round( t.getBestellung().getBestellmenge() * 100.0) / 100.0); 
         				double sumTakenFromPerson = (Math.round( mapAmountForOrder.get(f.getFrischbestand()) * 100.0) / 100.0);
             	    		BestellungBuyEntity  bbe = null;
             	    		//Schauen ob es ein Discrepancy Objekt zu dieser Bestellung gibt
             	    		for(BestellungBuyEntity bestellung : bestellungen) {
             	    			if(bestellung.getBestellung().getId().equalsIgnoreCase(t.getBestellung().getId())) {
             	    				bbe = bestellung;
             	    				break;
             	    			}
             	    		}
             	    		if(bbe != null) {
             	    			double sumToAdjust; 
             	    			if(bbe.getBestellung().isDone()) {
             	    				sumToAdjust = -t.getAmount();
             	    			} else {
             	    				 sumToAdjust = sumOrderedFromPerson - sumTakenFromPerson;
             	    				bbe.getBestellung().setDone(true);
             	    			}
             	    			//Rest kann hier nur vor kommen wenn es z.B. kein zuViel Objekt gab, ein user aber weniger nimmt als er bestellt hat
             	    			double rest = adjustNonMixDiscrepency(discrepancies, sumToAdjust, bbe );
             	    			if(rest != 0) {
             	    				DiscrepancyEntity disEntity = new DiscrepancyEntity(UUID.randomUUID().toString(), f.getFrischbestand(), 0, (float) rest, 0);
             	    				be.getDiscrepancy().add(discrepancyService.save(disEntity));
             	    			}
             	    			
             	    		}
         		//	}
         		}
         	});

        		}
        		
        	}
        }
       EinkaufEntity e = einkaufRepository.speichern(einkauf);
       if(be != null) {
        BestellUebersicht test = service.update(be);
       }
        // Speichere den Einkauf in der Datenbank
        return e;
         	 
    }

    private double adjustNonMixDiscrepency(List<DiscrepancyEntity> discrepancies, double sumToAdjust, BestellungBuyEntity bbe ) {
    	for(DiscrepancyEntity d : discrepancies) {
    		if(d.getBestand() instanceof FrischBestand) {
    			FrischBestand frisch = (FrischBestand) d.getBestand();
    			if(bbe.getBestellung() instanceof FrischBestellung) {
    				FrischBestellung fb = (FrischBestellung) bbe.getBestellung();
    			if(fb.getFrischbestand().getId().equalsIgnoreCase(frisch.getId())) {
    			
    			
    				d.setZuVielzuWenig((float) (d.getZuVielzuWenig() + sumToAdjust));
    				return 0;
    			}
    		}
    		}
    	}
    	//Eventuell neues Discrepancy objekt anlegen, TODO: Naschauen ob auch 0er in db gespeichert sind
    	return sumToAdjust;
    }

    
    
    
    /** Es muss sich erst alle offenen Orders über loadOpenOrders gezogen werden, dann kann jeweils die echt genommene Menge eingetragen werden
     * bei jedem Object und die fertige liste hier übergeben werden.
     * isReeleMengeAngegeben setzten falls User die angegeben hat, sonst wirds bei open orders wieder angezeigt!
     * 
     * 
     * @param personId
     * @param updatedOrders
     */
  
    
    public List<EinkaufEntity> findAllFromPerson(String personId) {
    	return einkaufRepository.alleVonPerson(personId);
    }

    public List<EinkaufEntity> all() {
    	return einkaufRepository.alle();
    }
    
    public EinkaufEntity findById(String id) {
    	return einkaufRepository.findeMitId(id).orElseThrow();
    }
    
    public void deleteById(String id) {
    	einkaufRepository.deleteById(id);
    }
    public double calculateTotalPrice(EinkaufEntity einkauf) {
    	return calculatePriceForBread(einkauf) + calculatePriceForFresh(einkauf) + calculatePriceForBestandBuy(einkauf);
    }
    
    public double calculatePriceForBread(EinkaufEntity einkauf) {
    	double price = 0d;
    	if(einkauf.getBestellungsEinkauf() != null) {
	    	for(BestellungBuyEntity ebv : einkauf.getBestellungsEinkauf()) {
	    		if(ebv.getBestellung() instanceof BrotBestellung) {
	    			double real = ebv.getAmount();
	    			BrotBestellung brot = (BrotBestellung) ebv.getBestellung();
	    			price = price + real * brot.getBrotBestand().getPreis();
	    		}
	    	}
    	}

    	return price;
    }
    
    public double calculatePriceForBestandBuy(EinkaufEntity einkauf) {
    	double price = 0d;
    	if(einkauf.getBestandEinkauf() == null) {
    		return price;
    	}
    	for(BestandBuyEntity be : einkauf.getBestandEinkauf()) {
    		if(be.getBestand() instanceof Produkt) {
    			price = price + be.getBestand().getPreis() * be.getAmount();
    		}
    	}
    	return price;
    }
    
    public double calculateDeliveryCostForShopping(EinkaufEntity ee) {
    	double price = calculatePriceForFresh(ee) + calculatePriceForTooMuch(ee);
    	double percent = configService.getConfig().get().getDeliverycost() / 100;
    	
    	
    	return price * percent;
    }
    
    public double calculatePriceForFresh(EinkaufEntity einkauf) {
    	double price = 0d;
    	if(einkauf.getBestellungsEinkauf() != null) { 
	    	for(BestellungBuyEntity ebv : einkauf.getBestellungsEinkauf()) {
	    		if(ebv.getBestellung() instanceof FrischBestellung) {
	    			double real = ebv.getAmount();
	    			FrischBestellung frisch = (FrischBestellung) ebv.getBestellung();
	    			price = price + real * frisch.getFrischbestand().getPreis();
	    		}
	    	}
    	}

    	return price;
    }
    
    public double calculatePriceForTooMuch(EinkaufEntity einkauf) {
    	double price = 0d;
    	if(einkauf.getTooMuchEinkauf() != null) { 
	    	for(TooMuchBuyEntity ebv : einkauf.getTooMuchEinkauf()) {
	    			double real = ebv.getAmount();
	    			price = price + real * ebv.getDiscrepancy().getBestand().getPreis();
	    	}
    	}

    	return price;
    }
    

	
}
