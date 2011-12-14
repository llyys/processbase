package ee.kovmen.entities;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import javax.persistence.Entity;

import java.util.Set;


@Entity
@Table(name = "TEENUS")
public class Teenus implements Serializable{
	
	 @Id
	 @GeneratedValue(strategy=GenerationType.AUTO)
	 private Long id;
	 
	 @ManyToMany()
	 @JoinTable(name = "OIGUSAKTI_TEENUSED")
	 private Set<Oigusakt> oigusaktid;
	
	 private String name;	
	 private String descrition;
		
		
	 public Long getId() {
	        return id;
     }

    private void setId(Long id) {
        this.id = id;
    }
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescrition() {
		return descrition;
	}
	public void setDescrition(String descrition) {
		this.descrition = descrition;
	}
		
	public void setOigusaktid(Set<Oigusakt> oigusaktid) {
		this.oigusaktid = oigusaktid;
	}
	
	
	public Set<Oigusakt> getOigusaktid() {
		return this.oigusaktid;
	}
	
}
