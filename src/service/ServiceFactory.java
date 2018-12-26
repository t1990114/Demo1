package service;

import java.util.WeakHashMap;
@SuppressWarnings("rawtypes")
public class ServiceFactory {
	
	private final static WeakHashMap<String,ICommonService> map = new WeakHashMap<>();
	public static ICommonService getService(String name){
		ICommonService service = map.get(name);
		if(service!=null){
			return service;
		}else{
			return createService(name);
		}
	}
	private static ICommonService createService(String name) {
		ICommonService service = null;
		if("user".equals(name)){
			service = new UserService();
			map.put("user", service);
		}
		if("friendGroup".equals(name)){
			service = new FriendGroupService();
			map.put("friendGroup", service);
		}
		if("friendGroupUser".equals(name)){
			service = new FriendGroupUserService();
			map.put("friendGroupUser", service);
		}
		if("msg".equals(name)){
			service = new MsgService();
			map.put("msg", service);
		}
		if("group".equals(name)){
			service = new GroupService();
			map.put("group", service);
		}
		if("userGroup".equals(name)){
			service = new UserGroupService();
			map.put("userGroup", service);
		}
		return service;
	}
}
