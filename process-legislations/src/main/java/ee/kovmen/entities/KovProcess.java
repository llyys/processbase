package ee.kovmen.entities;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="kov_process")
public class KovProcess {
	@Id
	@GeneratedValue(strategy=javax.persistence.GenerationType.AUTO)
	Long id;
	
	@Column
	String name;
	
	@Column
	String description;
	
	@ManyToMany
	private	Set<KovLegislation> legislations;

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

	public Set<KovLegislation> getLegislations() {
		return legislations;
	}

	public void setLegislations(Set<KovLegislation> legislations) {
		this.legislations = legislations;
	}
	
}
