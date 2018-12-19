package entity;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.validator.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class User implements Serializable{
	
	private static final long serialVersionUID = 3206107568609508997L;
	
	 private String userId;

	@NotNull(message="(user.not.null)")
	@Size(min=2,max=15,message="(user.userNickname.length.error)")
	private String userNickname;
	
	@NotNull(message="(user.not.null)")
	@Size(min=6,max=15,message="(user.userPassword.length.error)")
	private String userPassword;
	
	@NotNull(message="(user.not.null)")
	@Email(message="(user.userEmail.not.correct")
	private String userEmail;
	private Integer actiState;
	private String actiCode;
	private Date tokenExptime;
	private String salt;
	
	public static final int ACTIVATION_SUCCESSFUL = 1;
	public static final int ACTIVATION_UNSUCCESSFUL = 0;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserNickname() {
		return userNickname;
	}
	public void setUserNickname(String userNickname) {
		this.userNickname = userNickname;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public Integer getActiState() {
		return actiState;
	}
	public void setActiState(Integer actiState) {
		this.actiState = actiState;
	}
	public String getActiCode() {
		return actiCode;
	}
	public void setActiCode(String actiCode) {
		this.actiCode = actiCode;
	}
	public Date getTokenExptime() {
		return tokenExptime;
	}
	public void setTokenExptime(Date tokenExptime) {
		this.tokenExptime = tokenExptime;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	
}
