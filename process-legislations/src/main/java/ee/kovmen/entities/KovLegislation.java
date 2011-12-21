package ee.kovmen.entities;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="kov_legislation")
public class KovLegislation  implements Serializable{
	@Id
	@GeneratedValue(strategy=javax.persistence.GenerationType.AUTO)
	Long id;
	
	@Column
	String name;
	
	@Column
    String url;
	
	@Column
    String type;
	
	@ManyToOne(targetEntity=KovServiceCategory.class)
    KovServiceCategory category;
	
	//this will create new sub table, that allows extended OneToMany mapping
	@OneToMany
	private	Set<KovProcess> processes;
	
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
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public KovServiceCategory getCategory() {
		return category;
	}
	public void setCategory(KovServiceCategory category) {
		this.category = category;
	}
	public void setProcesses(Set<KovProcess> processes) {
		this.processes = processes;
	}
	public Set<KovProcess> getProcesses() {
		return processes;
	}
	
}
