package com.hfax.ucard.bean;

import java.io.Serializable;

/**
 * 图片验证码
 *
 * @author SGY
 * @date 2018/4/10
 */

public class ImageCodeBean implements Serializable {
    private static final long serialVersionUID = 4094367956227192381L;

    public String captchaId;
    public String img;
}
