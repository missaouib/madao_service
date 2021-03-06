package com.codeway.user.service;

import com.codeway.api.base.LoginLogServiceRpc;
import com.codeway.exception.custom.ResourceNotFoundException;
import com.codeway.exception.custom.UserException;
import com.codeway.model.dto.user.ResourceDto;
import com.codeway.model.dto.user.RoleDto;
import com.codeway.model.dto.user.UserDto;
import com.codeway.model.pojo.user.User;
import com.codeway.model.pojo.user.UserRole;
import com.codeway.user.dao.ResourceDao;
import com.codeway.user.dao.RoleDao;
import com.codeway.user.dao.UserDao;
import com.codeway.user.dao.UserRoleDao;
import com.codeway.user.mapper.ResourceMapper;
import com.codeway.user.mapper.RoleMapper;
import com.codeway.user.mapper.UserMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户服务
 **/
@Service
public class UserService {

	private final UserDao userDao;
	private final UserRoleDao userRoleDao;
	private final UserMapper userMapper;
	private final RoleMapper roleMapper;
	private final RoleDao roleDao;
	private final ResourceDao resourceDao;
	private final ResourceMapper resourceMapper;


	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	private final JPAQueryFactory jpaQueryFactory;

	public UserService(UserDao userDao,
	                   UserMapper userMapper,
	                   BCryptPasswordEncoder bCryptPasswordEncoder,
	                   LoginLogServiceRpc loginLogServiceRpc,
	                   RoleDao roleDao,
	                   ResourceDao resourceDao,
	                   UserRoleDao userRoleDao,
	                   RoleMapper roleMapper,
	                   ResourceDao resourceDao1,
	                   ResourceMapper resourceMapper, JPAQueryFactory jpaQueryFactory) {
		this.userDao = userDao;
		this.userMapper = userMapper;
		this.roleDao = roleDao;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.userRoleDao = userRoleDao;
		this.roleMapper = roleMapper;
		this.resourceDao = resourceDao1;
		this.resourceMapper = resourceMapper;
		this.jpaQueryFactory = jpaQueryFactory;
	}

	/**
	 * 注册用户
	 *
	 * @param userDto
	 */
	public void registerUser(UserDto userDto) {
		//加密后的密码
		String bCryptPassword = bCryptPasswordEncoder.encode(userDto.getPassword());
		userDto.setPassword(bCryptPassword);
		userDao.save(userMapper.toEntity(userDto));
	}


	public Page<UserDto> findByCondition(UserDto userDto, Pageable pageable) {

		Specification<User> condition = (root, query, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (StringUtils.isNotEmpty(userDto.getUserName())) {
				predicates.add(builder.like(root.get("userName"), "%" + userDto.getUserName() + "%"));
			}
			if (userDto.getStatus() != null) {
				predicates.add(builder.equal(root.get("status"), userDto.getStatus()));
			}
			if (StringUtils.isNotEmpty(userDto.getId())) {
				predicates.add(builder.equal(root.get("id"), userDto.getId()));
			}
			if (StringUtils.isNotEmpty(userDto.getAccount())) {
				predicates.add(builder.like(root.get("account"), "%" + userDto.getAccount() + "%"));
			}
			if (StringUtils.isNotEmpty(userDto.getPhone())) {
				predicates.add(builder.like(root.get("phone"), "%" + userDto.getPhone() + "%"));
			}
			Predicate[] ps = new Predicate[predicates.size()];
			return query.where(builder.and(predicates.toArray(ps))).getRestriction();
		};
		Page<UserDto> result = userDao.findAll(condition, pageable).map(userMapper::toDto);

		result.getContent().stream().map(this::assembleUserRoleData);


		return result;

	}

	public void deleteByIds(List<String> userIds) {
		userDao.deleteBatch(userIds);
		userRoleDao.deleteByUserIdIn(userIds);
	}

	/**
	 * 更新用户基础信息，关联的角色
	 *
	 * @param userDto 用户实体
	 */
	public void updateByPrimaryKey(UserDto userDto) {
		List<UserRole> userRoles = userDto.getRoles().stream()
				.map(user -> new UserRole(userDto.getId(), user.getId()))
				.collect(Collectors.toList());

		userDao.save(userMapper.toEntity(userDto));

		userRoleDao.deleteByUserIdIn(Collections.singletonList(userDto.getId()));

		userRoleDao.saveAll(userRoles);
	}


	/**
	 * 修改密码
	 * @param oldPassword 老密码
	 */
	public void changePassword(String userId, String oldPassword, String newOnePass) {
		User userInfo = userDao.findById(userId).orElseThrow(() -> new ResourceNotFoundException("用户不存在！"));
		if (!bCryptPasswordEncoder.matches(oldPassword, userInfo.getPassword())) {
			throw new UserException("密码不匹配！");
		}
		userInfo.setPassword(bCryptPasswordEncoder.encode(newOnePass));
		userDao.save(userInfo);
	}

	/**
	 * 获取用户角色权限
	 *
	 * @param id 用户id
	 */
	public UserDto getUserPermission(String id) {
		UserDto userDto = userDao.findById(id)
				.map(userMapper::toDto)
				.orElseThrow(ResourceNotFoundException::new);

		List<RoleDto> roles = roleDao.findRolesOfUser(id).map(roleMapper::toDto).orElse(Collections.emptyList());

		roles.forEach(
				e -> {
					Set<ResourceDto> resources = Optional.ofNullable(
							resourceDao.findResourceByRoleIds(Collections.singletonList(e.getId())))
							.map(resourceMapper::toDto)
							.orElse(Collections.emptySet());
					e.setResources(resources);
				}
		);

		userDto.setRoles(new HashSet<>(roles));

		return userDto;
	}

	/**
	 * 查询当前角色的用户列表
	 *
	 * @param roleDto 角色
	 * @return List<User>
	 */
	public List<UserDto> findUsersOfRole(RoleDto roleDto) {
		return userDao.findUsersOfRole(roleDto.getId())
				.map(userMapper::toDto)
				.orElseThrow(ResourceNotFoundException::new);
	}

	public UserDto findById(String userId) {
		UserDto userDto = userDao.findById(userId)
				.map(userMapper::toDto)
				.orElseThrow(ResourceNotFoundException::new);

		return assembleUserRoleData(userDto);
	}

	public UserDto findByCondition(UserDto userDto) {

		Specification<User> condition = (root, query, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (StringUtils.isNotEmpty(userDto.getId())) {
				predicates.add(builder.equal(root.get("id"), userDto.getId()));
			}
			if (StringUtils.isNotEmpty(userDto.getAccount())) {
				predicates.add(builder.equal(root.get("account"), userDto.getAccount()));
			}
			if (StringUtils.isNotEmpty(userDto.getPhone())) {
				predicates.add(builder.equal(root.get("phone"), userDto.getPhone()));
			}
			Predicate[] ps = new Predicate[predicates.size()];
			return query.where(builder.and(predicates.toArray(ps))).getRestriction();
		};

		UserDto userInfo = userDao.findOne(condition)
				.map(userMapper::toDto)
				.orElse(null);

		return assembleUserRoleData(userInfo);
	}

	public void updateUserProfile(UserDto userDto) {
		userDao.save(userMapper.toEntity(userDto));
	}


	/**
	 * 组装用户角色数据
	 *
	 * @param userDto userDto
	 * @return UserDto
	 */
	private UserDto assembleUserRoleData(UserDto userDto) {
		if (userDto == null) {
			return null;
		}
		List<RoleDto> rolesOfUser = roleDao.findRolesOfUser(userDto.getId())
				.map(roleMapper::toDto)
				.orElseThrow(ResourceNotFoundException::new);
		userDto.setRoles(new HashSet<>(rolesOfUser));
		return userDto;
	}
}
