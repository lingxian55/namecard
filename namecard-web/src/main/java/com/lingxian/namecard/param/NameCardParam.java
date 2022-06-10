package com.lingxian.namecard.param;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import io.netty.handler.codec.string.StringEncoder;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @Description
 * @Author lingxian36158
 * @Date 2022-05-29 16:54
 * @Since 1.0
 */
@Data
public class NameCardParam {
    String email;
    String name;
    String publicKey;
    String code;
    String address;
    String encryptData;
}
