package ee.kovmen.entities;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.Entity;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "Oigusakt")
@Entity
public class Oigusakt implements Serializable{
	 @Id
	 @GeneratedValue(strategy=GenerationType.AUTO)
	 private Long id;
	 
	 public Long getId() {
	        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }
    @Column(name = "name")
	private String name;
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
	 @Column(name = "descritpion")
	private String description;
	 @Column(name = "url")
	private String url;
	
	@ManyToMany
	private Set<Teenus> teenused;
}