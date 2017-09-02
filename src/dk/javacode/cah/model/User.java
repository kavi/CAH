package dk.javacode.cah.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import dk.javacode.srsm.annotations.Column;
import dk.javacode.srsm.annotations.Table;

@Table(name = "user")
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "id", primaryKey = true)
	private Integer id;

	@Column(name = "name")
	@Pattern(regexp = "[0-9|\\w]+", message = "Username may only contain letters and digits")
	@NotNull()
	@Size(min = 3, max = 64)
	private String name;

	@Column(name = "password", writeOnly = true)
	@NotNull()
	@Size(min = 4)
	private String password;

	@Column(name = "email")
	private String email;

	@Column(name = "family_filter")
	private Boolean familyFilter;

	public User() {
		super();
	}

	public User(int id, String name, String password, String email) {
		super();
		this.id = id;
		this.name = name;
		this.password = password;
		this.email = email;
	}

	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean isFamilyFilter() {
		return familyFilter;
	}

	public void setFamilyFilter(Boolean familyFilter) {
		this.familyFilter = familyFilter;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", password=" + (password != null ? "YES" : "NO") + ", email=" + email + ", familyFilter="
				+ familyFilter + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
