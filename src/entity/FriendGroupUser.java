package entity;

public class FriendGroupUser implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	
	private int friendGroupId;
	private int userId;
	public int getFriendGroupId() {
		return friendGroupId;
	}
	public void setFriendGroupId(int friendGroupId) {
		this.friendGroupId = friendGroupId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
}
