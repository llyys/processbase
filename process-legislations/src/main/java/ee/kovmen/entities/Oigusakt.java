package ee.kovmen.entities;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import javax.persistence.*;

@Entity
@Table(name="OIGUSAKT")
public class Oigusakt implements Serializable{
	 
	@Id 	 
	private Long id;
	
	@Column(name = "name")
	private String name;
	 
	@Column(name = "descritpion")
	private String description;
	
	@Column(name = "url")
	private String url;
	
	@ManyToMany()
	@JoinTable(name = "OIGUSAKTI_TEENUSED")
	private Set<Teenus> teenused;
	
	public Long getId() {
		return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
	public Set<Teenus> getTeenused() {
		return teenused;
	}
	public void setTeenused(Set<Teenus> teenused) {
		this.teenused = teenused;
	}
	
	
}