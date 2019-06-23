/**
 * 
 */
package com.youyd.auth.validate;


import com.youyd.constant.CommonConst;

/**
 * 校验码类型
 * @author : LGG
 * @create : 2019-06-18 14:34
 **/
public enum ValidateCodeType {
	
	/**
	 * 短信验证码
	 */
	SMS {
		@Override
		public String getParamNameOnValidate() {
			return CommonConst.DEFAULT_PARAMETER_NAME_CODE_SMS;
		}
	},
	/**
	 * 图片验证码
	 */
	CAPTCHA {
		@Override
		public String getParamNameOnValidate() {
			return CommonConst.DEFAULT_PARAMETER_NAME_CODE_IMAGE;
		}
	};

	/**
	 * 校验时从请求中获取的参数的名字
	 * @return
	 */
	public abstract String getParamNameOnValidate();

}
