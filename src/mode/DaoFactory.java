package mode;

import java.util.WeakHashMap;
@SuppressWarnings("rawtypes")
public class DaoFactory {
	
	private final static WeakHashMap<String,ICommonDao> map = new WeakHashMap<>();
	public static ICommonDao getDao(String name){
		ICommonDao dao= map.get(name);
		if(dao!=null){
			return dao;
		}else{
			return createDao(name);
		}
	}
	private static ICommonDao createDao(String name) {
		ICommonDao dao = null;
		if("user".equals(name)){
			dao = new UserDao();
			map.put("user",dao);
			return dao;
		}
		if("friendGroup".equals(name)){
			dao = new FriendGroupDao();
			map.put("friendGroup",dao);
			return dao;
		}
		if("friendGroupUser".equals(name)){
			dao = new FriendGroupUserDao();
			map.put("friendGroupUser",dao);
			return dao;
		}
		if("msg".equals(name)){
			dao = new MsgDao();
			map.put("msg",dao);
			return dao;
		}
		if("group".equals(name)){
			dao = new GroupDao();
			map.put("group",dao);
			return dao;
		}
		if("userGroup".equals(name)){
			dao = new UserGroupDao();
			map.put("userGroup",dao);
			return dao;
		}
		return null;
	}
	
}
