package service;

import java.util.List;

import mode.DaoFactory;
import entity.User;
@SuppressWarnings("unchecked")
public class UserService extends ImplCommonSevice<User>{

	@Override
	public User findById(int id) {
		return (User) DaoFactory.getDao("user").findById(id);
	}

	@Override
	public User findByCondition(String... conditions) {
		return (User) DaoFactory.getDao("user").findByConditions(conditions);
	}

	
	@Override
	public int update(User t) {
		return DaoFactory.getDao("user").update(t);
	}
	

	@Override
	public List<User> listPart(String... conditions) {
		return DaoFactory.getDao("user").listPart(conditions);
	}

	@Override
	public int deleteById(int id) {
		// TODO Auto-generated method stub
		return super.deleteById(id);
	}

	@Override
	public int delete(User t) {
		// TODO Auto-generated method stub
		return super.delete(t);
	}
	
}
