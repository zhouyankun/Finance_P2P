package zyk.finance.domain.admin;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_admin")
public class AdminModel implements Serializable{
	@Id
	@GeneratedValue
	@Column(name = "t_id")
	private int id;
	@Column(name = "t_username")
	private String username;
	@Column(name = "t_password")
	private String password;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "AdminModel [id=" + id + ", username=" + username + ", password=" + password + "]";
	}

}
