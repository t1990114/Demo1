package service;

import java.util.List;

import mode.DaoFactory;
import entity.Group;

public class GroupService extends ImplCommonSevice<Group>{

	@Override
	public int updateByConditions(String... conditions) {
		return DaoFactory.getDao("group").updateByCondition(conditions);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Group> listPart(String... conditions) {
		return DaoFactory.getDao("group").listPart(conditions);
	}

	@Override
	public List<Group> findByConditions(String... conditions) {
		// TODO Auto-generated method stub
		return super.findByConditions(conditions);
	}

	@Override
	public Group findById(int id) {
		// TODO Auto-generated method stub
		return super.findById(id);
	}

	@Override
	public Group findByCondition(String... conditions) {
		return (Group) DaoFactory.getDao("group").findByConditions(conditions);
	}

	@Override
	public int update(Group t) {
		// TODO Auto-generated method stub
		return super.update(t);
	}

	@Override
	public int deleteById(int id) {
		// TODO Auto-generated method stub
		return super.deleteById(id);
	}

	@Override
	public int delete(Group t) {
		// TODO Auto-generated method stub
		return super.delete(t);
	}

	
}
