package service;

import java.util.List;

import mode.DaoFactory;
import entity.FriendGroup;

public class FriendGroupService extends ImplCommonSevice<FriendGroup>{

	@Override
	public FriendGroup findById(int id) {
		return (FriendGroup) DaoFactory.getDao("friendGroup").findById(id);
	}


	@Override
	public FriendGroup findByCondition(String... conditions) {
		return (FriendGroup) DaoFactory.getDao("friendGroup").findByConditions(conditions);
	}

	@Override
	public int updateByConditions(String... conditions) {
		System.out.println("hello");
		return DaoFactory.getDao("friendGroup").updateByCondition(conditions);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FriendGroup> listPart(String... conditions) {
		return DaoFactory.getDao("friendGroup").listPart(conditions);
	}
	
	

}
