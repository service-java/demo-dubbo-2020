

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.reger.dubbo.annotation.Inject;
import com.reger.test.core.DubboLeaderApplication;
import com.reger.test.user.model.User;
import com.reger.test.user.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=DubboLeaderApplication.class)
public class JpaDobboConsumerTest {
	
	private static final Logger log = LoggerFactory.getLogger(JpaDobboConsumerTest.class);
    
	@Inject UserService userService;

	@Test
	public void testSave() {
		log.info("清理用户数据完毕");
		User user1 = userService.save("张三1", "张三的描述");
		log.info("保存用户数据完毕 {}",user1);
		User user2 = userService.save("张三2", "张三2的描述");
		log.info("保存用户数据完毕 {}",user2);
	}
	
	@Test
	public void testFindAll() {
		List<User> users = userService.findAll();
		log.info("查询得到的用户数据列表 {}",users);
	}
	
}
