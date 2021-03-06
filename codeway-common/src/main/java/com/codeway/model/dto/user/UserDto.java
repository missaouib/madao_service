package com.codeway.model.dto.user;

import com.codeway.enums.RegisteredType;
import com.codeway.model.BasePojo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "user", description = "用户实体类")
public class UserDto extends BasePojo implements Serializable {

	@ApiModelProperty("角色集合")
	private Set<RoleDto> roles = new HashSet<>();

	@ApiModelProperty("验证码")
	@Transient
	private String captcha;

	@ApiModelProperty("用户表主键")
	private String id;

	@NotNull(message = "账号不能为空")
	@ApiModelProperty("账号")
	private String account;

	@NotNull(message = "用户名不能为空")
	@ApiModelProperty("用户名")
	private String userName;

	@NotNull(message = "昵称不能为空")
	@ApiModelProperty("昵称")
	private String nickName;

	@NotNull(message = "密码不能为空")
	@ApiModelProperty("密码")
	private String password;

	@NotNull(message = "性别不能为空")
	@ApiModelProperty("性别")
	private Boolean sex;

	@ApiModelProperty(value = "出生年月日", example = "1")
	private Long birthday;

	@ApiModelProperty("头像")
	private String avatar;

	@ApiModelProperty("E-Mail")
	@NotNull(message = "E-Mail不能为空")
	@Pattern(regexp = "^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", message = "邮箱格式不正确!")
	private String email;

	@ApiModelProperty(value = "最后登陆日期", example = "1")
	private Long lastDate;

	@ApiModelProperty(value = "在线时长（分钟）", example = "1")
	private Long onlineTime;

	@ApiModelProperty("兴趣")
	private String interest;

	@ApiModelProperty("个性")
	private String personality;

	@ApiModelProperty(value = "粉丝数", example = "123")
	private Integer fansCount;

	@ApiModelProperty(value = "关注数", example = "123")
	private Integer followCount;

	@ApiModelProperty("手机")
	@NotNull(message = "手机不能为空")
	private String phone;

	@ApiModelProperty("联系地址")
	private String contactAddress;

	@ApiModelProperty("注册类型/方式")
	private RegisteredType registeredType;

	@ApiModelProperty(value = "是否锁定(0:未锁定,1已锁定)", example = "0")
	private Boolean status;

	public UserDto(String userId) {
		this.id = userId;
	}
}