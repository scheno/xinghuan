package com.schening.xinghuan.framework.common.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Key Value 的键值对
 *
 * @author shenchen
 * @version 1.0
 * @date 2023/2/8 16:33
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyValue<K, V> {

    K key;

    V value;

}
