package service;

import java.util.List;

import mode.DaoFactory;
import entity.FriendGroupUser;

public class FriendGroupUserService extends ImplCommonSevice<FriendGroupUser>{

	@SuppressWarnings("unchecked")
	@Override
	public List<FriendGroupUser> listPart(String... conditions) {
		return DaoFactory.getDao("friendGroupUser").listPart(conditions);
	}

	@Override
	public int updateByConditions(String... conditions) {
		return DaoFactory.getDao("friendGroupUser").updateByCondition(conditions);
	}

}
