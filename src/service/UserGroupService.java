package service;

import java.util.List;

import mode.DaoFactory;
import entity.UserGroup;

public class UserGroupService extends ImplCommonSevice<UserGroup>{

	@Override
	public int updateByConditions(String... conditions) {
		return DaoFactory.getDao("userGroup").updateByCondition(conditions);
	}

	@Override
	public List<UserGroup> listPart(String... conditions) {
		// TODO Auto-generated method stub
		return super.listPart(conditions);
	}

	@Override
	public List<UserGroup> findByConditions(String... conditions) {
		// TODO Auto-generated method stub
		return super.findByConditions(conditions);
	}

	@Override
	public UserGroup findByCondition(String... conditions) {
		// TODO Auto-generated method stub
		return super.findByCondition(conditions);
	}

	@Override
	public int update(UserGroup t) {
		// TODO Auto-generated method stub
		return super.update(t);
	}

	
}
