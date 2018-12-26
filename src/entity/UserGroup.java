package entity;

public class UserGroup implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	private int userId;
	private int GroupId;
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getGroupId() {
		return GroupId;
	}
	public void setGroupId(int groupId) {
		GroupId = groupId;
	}
	
}
