package ee.kovmen.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="kov_service_impl")
public class KovServiceImpl implements Serializable{
	
	@Id
	@GeneratedValue(strategy=javax.persistence.GenerationType.AUTO) 
	Long id;
	@Column
	String description;
	
	@Column
	Boolean is_active;

	@Column
	Long duration;

	@Column
	Double price;
	
	@ManyToOne
	KovService service;
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIs_active() {
		return is_active;
	}

	public void setIs_active(Boolean is_active) {
		this.is_active = is_active;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public KovService getService() {
		return service;
	}

	public void setService(KovService service) {
		this.service = service;
	}


}
