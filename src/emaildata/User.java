package emaildata;

public class User {
	private int id;
	private String email;
	private String name;
	public String getUsername() {
		return name;
	}
	public void setUsername(String name) {
		this.name = name;
	}
	public String getEmailAddress() {
		return email;
	}
	public void setEmailAddress(String email) {
		this.email = email;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
