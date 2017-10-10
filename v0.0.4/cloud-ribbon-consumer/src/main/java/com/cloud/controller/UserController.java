package com.cloud.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.cloud.model.User;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

/**
 * @author kenan.zhang
 * @version V1.0
 * @Description: 用户控制器,使用ribbon实现客户端负载均衡,cloud-provider为服务名
 * @date 2017/8/15
 */

@RestController
public class UserController {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    /**
     * 熔断实例,加上@HystrixCommand注解即可
     * @param id
     * @return user
     */
    @GetMapping("/queryUserById/{id}")
    @ResponseBody
    @HystrixCommand(fallbackMethod = "error")
    public User queryUserById(@PathVariable Long id) {
    	
    	ResponseEntity<User> responseEntity = this.restTemplate.getForEntity("http://cloud-provider/cloud-provider-context/queryUserById/" + id, User.class);
    	return responseEntity.getBody();
    	
    }

    /**
     * 非熔断实例,去掉@HystrixCommand注解即可
     * @param id
     * @return user
     */
    @GetMapping("/queryUser/{id}")
    @ResponseBody
    public User queryUser(@PathVariable Long id) {
        
    	ResponseEntity<User> responseEntity = this.restTemplate.getForEntity("http://cloud-provider/cloud-provider-context/queryUserById/" + id, User.class);
    	return responseEntity.getBody();
    	
    }


    public User error(Long id) {
    	System.err.println("id=================="+id);
        User user = new User();
        user.setAge(100);
        user.setBalance(new BigDecimal(100));
        user.setName("error");
        return user;
    }

}