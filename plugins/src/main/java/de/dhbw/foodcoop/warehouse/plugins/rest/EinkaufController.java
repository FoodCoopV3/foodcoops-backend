package de.dhbw.foodcoop.warehouse.plugins.rest;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import de.dhbw.foodcoop.warehouse.adapters.representations.EinkaufCreateRepresentation;
import de.dhbw.foodcoop.warehouse.adapters.representations.EinkaufRepresentation;
import de.dhbw.foodcoop.warehouse.adapters.representations.FrischBestandRepresentation;
import de.dhbw.foodcoop.warehouse.adapters.representations.mappers.EinkaufCreateToEntityMapper;
import de.dhbw.foodcoop.warehouse.adapters.representations.mappers.EinkaufToRepresentationMapper;
import de.dhbw.foodcoop.warehouse.adapters.representations.mappers.RepresentationToEinkaufMapper;
import de.dhbw.foodcoop.warehouse.application.einkauf.EinkaufService;
import de.dhbw.foodcoop.warehouse.domain.entities.BestandBuyEntity;
import de.dhbw.foodcoop.warehouse.domain.entities.BrotBestellung;
import de.dhbw.foodcoop.warehouse.domain.entities.EinkaufEntity;
import de.dhbw.foodcoop.warehouse.domain.entities.FrischBestellung;
import de.dhbw.foodcoop.warehouse.plugins.email.EmailService;
import de.dhbw.foodcoop.warehouse.plugins.helpObjects.BestandBuyCreator;
import de.dhbw.foodcoop.warehouse.plugins.pdf.PdfService;
import de.dhbw.foodcoop.warehouse.plugins.rest.assembler.EinkaufModelAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
public class EinkaufController {
	
    private final EinkaufService einkaufService;
    
    @Autowired
    private  RepresentationToEinkaufMapper toEinkauf;
    
    @Autowired
    private PdfService pdf;
    @Autowired
    private  EinkaufToRepresentationMapper toPresentation;
    
    @Autowired
    private  EinkaufCreateToEntityMapper createMapper;
    
    @Autowired
    private  EinkaufModelAssembler assembler;
    
    @Autowired
    private EmailService emailService;
    

    @Autowired
    public EinkaufController(EinkaufService einkaufService) {
    	this.einkaufService = einkaufService;
    }
    

    @GetMapping("/einkauf")
    public CollectionModel<EntityModel<EinkaufRepresentation>> all() {
    	
     	List<EinkaufEntity> einkauf = einkaufService.all();
    	List<EntityModel<EinkaufRepresentation>> e = new ArrayList<>();
    	for(EinkaufEntity f : einkauf) {
    		EinkaufRepresentation fr = (EinkaufRepresentation) toPresentation.apply(f);
    		EntityModel<EinkaufRepresentation> em = assembler.toModel(fr);
    		e.add(em);
    	}
       
        return CollectionModel.of(e,
                linkTo(methodOn(EinkaufController.class).all()).withSelfRel());
    }
    
    @PostMapping("/einkauf/pdf/{id}")
    public byte[] sendPdfAndMail(@RequestBody String email, @PathVariable String id) {
    			System.out.println(email + " <--- Email");
    		
  	       try {
  	    	   EinkaufEntity einkauf = einkaufService.findById(id);
  	    	  String fileName = "Einkauf-FoodCoop-" + einkauf.getPersonId() + "-" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ".pdf";
  	        byte[] pdfd = pdf.createEinkauf(einkauf);
  	      
  	        
  	        StringBuilder frischString = new StringBuilder();
  	        einkauf.getBestellungsEinkauf().stream()
  	    	.forEach(item -> {
  	    		if(item.getBestellung() instanceof FrischBestellung) {
  	    			FrischBestellung item2 = (FrischBestellung) item.getBestellung();
  	    	        	frischString.append(item2.getFrischbestand().getName() + "  je ");
  	    	        	frischString.append(item2.getFrischbestand().getPreis() + " €   ");
  	    	        	frischString.append("Bestellt: " + item2.getBestellmenge() + "   ");
  	    	        	frischString.append("Genommen: " + item.getAmount() + "\n");
  	    		}
  	    	});

  	        StringBuilder brotString = new StringBuilder();
  	        einkauf.getBestellungsEinkauf().stream()
  	    	.forEach(item -> {
  	    		if(item.getBestellung() instanceof BrotBestellung) {
  	    			BrotBestellung item2 = (BrotBestellung) item.getBestellung();
  	    	        	frischString.append(item2.getBrotBestand().getName() + "  je ");
  	    	        	frischString.append(item2.getBrotBestand().getPreis() + " €   ");
  	    	        	frischString.append("Bestellt: " + item2.getBestellmenge() + "   ");
  	    	        	frischString.append("Genommen: " + item.getAmount() + "\n");
  	    		}
  	    	});
  	        StringBuilder lagerString = new StringBuilder();
  	        einkauf.getBestandEinkauf().forEach(item -> {
  	        	frischString.append(item.getBestand().getName() + "  je ");
  	        	frischString.append(item.getBestand().getPreis() + " €   ");
  	        	frischString.append("Genommen: " + item.getAmount() + "\n");
  	        });
  	        
  	        float lieferkosten = (float) (einkauf.getFreshPriceAtTime() * 0.05);
  	        float gesamt = (float) (lieferkosten + einkauf.getTotalPriceAtTime());
  	        
  	        String text = "Hallo " + einkauf.getPersonId()+ ",\n"
  	        		+ "\n"
  	        		+ "Dein Einkauf bei der Foodcoop in der Karlsruher Nordstadt am "+ LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " war erfolgreich!\n"
  	        				+ "Anbei befindet sich der Beleg für den Einkauf als Pdf.\n"
  	        				+ "Hier nochmal eine Auflistung deines Einkaufs\n"
  	        				+ "\n"
  	        				+ "Frischwaren:\n"
  	        				+ frischString.toString() + "\n\n\n"
  	        						+ "Brot: \n"
  	        						+ brotString.toString() + "\n\n\n"
  	        								+ "Lagerware: \n"
  	        								+ lagerString.toString() + "\n\n\n"
  	        										+ "Gesamtpreis: " + (Math.round(gesamt * 100.0) / 100.0)  + "€ \n"
  	        												+ "\n"
  	        												+ "Weitere Details können aus der PDF entnommen werden! \n\n\n"
  	        												+ "Viele Grüße\n"
  	        												+ "Ihre Foodcoop Karlsruhe Nordstadt";
  	        
  	        try {
  				emailService.sendSimpleMessage(email, "Einkauf bei der FoodCoop Karlsruhe am " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), text, pdfd, fileName);
  			} catch (MessagingException e1) {
  				// TODO Auto-generated catch block
  				e1.printStackTrace();
  			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
  	       
    }
    
    @GetMapping("/einkauf/{id}")
    public EntityModel<EinkaufRepresentation> one(@PathVariable String id) {
    	EinkaufEntity einkauf = einkaufService.findById(id);
    	EinkaufRepresentation presentation = (EinkaufRepresentation) toPresentation.apply(einkauf);
        return assembler.toModel(presentation);
    }


    
    @PostMapping(value = "/einkaufe/create/bestandBuyObject")
    public BestandBuyEntity getBBEFromData(@RequestBody BestandBuyCreator creator) {
    	return einkaufService.createBestandBuyEntityForPersonOrder(creator.getBestandEntity(), creator.getAmount());
    } 
    
    
    @PostMapping("/einkauf")
    @Operation(summary = "Führe einen Einkauf durch", description = "Liefert ein Einkaufs Entity zurück")
    @ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success", content = {
					@Content(schema = @Schema(implementation = EinkaufRepresentation.class)) }),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
			@ApiResponse(responseCode = "401", description = "Not Authorized", content = @Content),
			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content) })
    public ResponseEntity<?> executeShopping(@io.swagger.v3.oas.annotations.parameters.RequestBody(content = {@Content(schema = @Schema(implementation = EinkaufCreateRepresentation.class)),@Content(schema = @Schema(implementation = FrischBestandRepresentation.class)) }) @RequestBody EinkaufCreateRepresentation newEinkauf) {
        String id = newEinkauf.getId() == null ||
        		newEinkauf.getId().equals("undefined") ?
                UUID.randomUUID().toString() :
                	newEinkauf.getId();
        newEinkauf.setId(id);
        newEinkauf.getBestellungsEinkauf().stream().forEach(d -> d.setId(UUID.randomUUID().toString()));
        EinkaufEntity e = createMapper.apply(newEinkauf);
        EinkaufEntity einkauf;
		try {
			einkauf = einkaufService.einkaufDurchführen(e.getPersonId(), e.getBestellungsEinkauf(), e.getBestandEinkauf());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e1.getCause().toString());
		}
        EntityModel<EinkaufRepresentation> entityModel = assembler.toModel( toPresentation.apply(einkauf));
        
      
        
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }
    
    
    @DeleteMapping("/einkauf/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {

        einkaufService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
    
}
