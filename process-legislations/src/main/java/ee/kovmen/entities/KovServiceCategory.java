package ee.kovmen.entities;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * Kovmen teenuste katekooria, READ only 
 * @author lauri
 *
 */
@Entity(name="BN_CATEGORY")
public class KovServiceCategory  implements Serializable{
	@Id
	@Column(name="DBID_")
	@GeneratedValue(strategy=javax.persistence.GenerationType.AUTO)
	Long id;
	
	@Column
	String name;
	
	@OneToMany
	Set<KovLegislation> legislations;

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

		
	public void setLegislations(Set<KovLegislation> legislations) {
		this.legislations = legislations;
	}

	public Set<KovLegislation> getLegislations() {
		return legislations;
	}
	
	@Override
	public String toString(){
		return name;		
	}

	
}
