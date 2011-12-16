package ee.kovmen.entities;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="kov_service")
public class KovService  implements Serializable{
	@Id
	@GeneratedValue(strategy=javax.persistence.GenerationType.AUTO) 
	Long id;
	
	@Column
	String name;
	
	@Column
	String description;
	 
	@ManyToOne
	KovServiceCategory category;
		
	@OneToMany
	Set<KovServiceImpl> services;
}
