package ee.kovmen.entities;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="bn_proc_def")
public class KovProcess {
	@Id
	@GeneratedValue(strategy=javax.persistence.GenerationType.AUTO)
	@Column(name="DBID_")
	Long id;
	
	@Column(name="proc_uuid_")
	String uuid;
	
	@Column(name="LABEL_OR_NAME_")
	String name;
	
	@Column(name="DESCRIPTION_")
	String description;

	@Column(name="VERSION_")
	private	String version;

	@Column(name="STATE_")
	private	String state;
	
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

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}
	@Override
	public String toString(){
		return this.name+ " "+this.version;
		
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
