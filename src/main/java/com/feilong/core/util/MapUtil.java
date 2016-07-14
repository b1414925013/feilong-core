/*
 * Copyright (C) 2008 feilong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.feilong.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.feilong.core.bean.PropertyUtil;
import com.feilong.tools.jsonlib.JsonUtil;

import static com.feilong.core.Validator.isNotNullOrEmpty;
import static com.feilong.core.Validator.isNullOrEmpty;
import static com.feilong.core.bean.ConvertUtil.toArray;

/**
 * {@link Map}工具类.
 * 
 * <h3>hashCode与equals:</h3>
 * <blockquote>
 * 
 * <p>
 * hashCode重要么?<br>
 * 对于{@link java.util.List List}集合、数组而言,不重要,他就是一个累赘; <br>
 * 但是对于{@link java.util.HashMap HashMap}、{@link java.util.HashSet HashSet}、 {@link java.util.Hashtable Hashtable} 而言,它变得异常重要.
 * </p>
 * 
 * <p>
 * 在Java中hashCode的实现总是伴随着equals,他们是紧密配合的,你要是自己设计了其中一个,就要设计另外一个。
 * </p>
 * <p>
 * <img src="http://venusdrogon.github.io/feilong-platform/mysource/hashCode-and-equals.jpg"/>
 * </p>
 * 
 * 整个处理流程是:
 * <ol>
 * <li>判断两个对象的hashcode是否相等,若不等,则认为两个对象不等,完毕,若相等,则比较equals。</li>
 * <li>若两个对象的equals不等,则可以认为两个对象不等,否则认为他们相等。</li>
 * </ol>
 * </blockquote>
 * 
 * <h3>关于 {@link java.util.Map }:</h3>
 * 
 * <blockquote>
 * <table border="1" cellspacing="0" cellpadding="4" summary="">
 * <tr style="background-color:#ccccff">
 * <th align="left">interface/class</th>
 * <th align="left">说明</th>
 * </tr>
 * 
 * <tr valign="top">
 * <td>{@link java.util.Map Map}</td>
 * <td>
 * <ol>
 * <li>An object that maps keys to values.</li>
 * <li>A map cannot contain duplicate keys</li>
 * <li>Takes the place of the Dictionary class</li>
 * </ol>
 * </td>
 * </tr>
 * 
 * <tr valign="top" style="background-color:#eeeeff">
 * <td>{@link java.util.HashMap HashMap}</td>
 * <td>
 * <ol>
 * <li>Hash table based implementation of the Map interface.</li>
 * <li>permits null values and the null key.</li>
 * <li>makes no guarantees as to the order of the map</li>
 * </ol>
 * <p>
 * 扩容:
 * </p>
 * <blockquote>
 * <ol>
 * <li>{@link java.util.HashMap HashMap} 初始容量 {@link java.util.HashMap#DEFAULT_INITIAL_CAPACITY }是16,DEFAULT_LOAD_FACTOR 是0.75
 * <code>java.util.HashMap#addEntry</code> 是 2 * table.length 2倍<br>
 * </ol>
 * </blockquote>
 * </td>
 * </tr>
 * 
 * <tr valign="top">
 * <td>{@link java.util.LinkedHashMap LinkedHashMap}</td>
 * <td>
 * <ol>
 * <li>Hash table and linked list implementation of the Map interface,</li>
 * <li>with predictable iteration order.</li>
 * </ol>
 * Note that: insertion order is not affected if a key is re-inserted into the map.
 * </td>
 * </tr>
 * 
 * <tr valign="top" style="background-color:#eeeeff">
 * <td>{@link java.util.TreeMap TreeMap}</td>
 * <td>
 * <ol>
 * <li>A Red-Black tree based NavigableMap implementation</li>
 * <li>sorted according to the natural ordering of its keys, or by a Comparator.</li>
 * <li>默认情况 key不能为null,如果传入了 <code>NullComparator</code>那么key 可以为null.</li>
 * </ol>
 * </td>
 * </tr>
 * 
 * <tr valign="top">
 * <td>{@link java.util.Hashtable Hashtable}</td>
 * <td>
 * <ol>
 * <li>This class implements a hashtable, which maps keys to values.</li>
 * <li>synchronized.</li>
 * <li>Any non-null object can be used as a key or as a value.</li>
 * </ol>
 * </td>
 * </tr>
 * 
 * <tr valign="top" style="background-color:#eeeeff">
 * <td>{@link java.util.Properties Properties}</td>
 * <td>
 * <ol>
 * <li>The Properties class represents a persistent set of properties.</li>
 * <li>can be saved to a stream or loaded from a stream.</li>
 * <li>Each key and its corresponding value in the property list is a string.</li>
 * </ol>
 * </td>
 * </tr>
 * 
 * <tr valign="top">
 * <td>{@link java.util.IdentityHashMap IdentityHashMap}</td>
 * <td>
 * <ol>
 * <li>using reference-equality in place of object-equality when comparing keys (and values).</li>
 * <li>使用==代替equals()对key进行比较的散列表.专为特殊问题而设计的</li>
 * </ol>
 * <p style="color:red">
 * 注意:此类不是 通用 Map 实现！它有意违反 Map 的常规协定,此类设计仅用于其中需要引用相等性语义的罕见情况
 * </p>
 * </td>
 * </tr>
 * 
 * <tr valign="top" style="background-color:#eeeeff">
 * <td>{@link java.util.WeakHashMap WeakHashMap}</td>
 * <td>
 * <ol>
 * <li>A hashtable-based Map implementation with weak keys.</li>
 * <li>它对key实行"弱引用",如果一个key不再被外部所引用,那么该key可以被GC回收</li>
 * </ol>
 * </td>
 * </tr>
 * 
 * <tr valign="top">
 * <td>{@link java.util.EnumMap EnumMap}</td>
 * <td>
 * <ol>
 * <li>A specialized Map implementation for use with enum type keys.</li>
 * <li>Enum maps are maintained in the natural order of their keys</li>
 * <li>不允许空的key</li>
 * </ol>
 * </td>
 * </tr>
 * </table>
 * </blockquote>
 * 
 * @author <a href="http://feitianbenyue.iteye.com/">feilong</a>
 * @see java.util.AbstractMap.SimpleEntry
 * @see org.apache.commons.collections4.MapUtils
 * @see "com.google.common.collect.Maps"
 * @since 1.0.0
 */
public final class MapUtil{

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MapUtil.class);

    /** Don't let anyone instantiate this class. */
    private MapUtil(){
        //AssertionError不是必须的. 但它可以避免不小心在类的内部调用构造器. 保证该类在任何情况下都不会被实例化.
        //see 《Effective Java》 2nd
        throw new AssertionError("No " + getClass().getName() + " instances for you!");
    }

    /**
     * 将多值的参数<code>arrayValueMap</code> 转成单值的参数map.
     * 
     * <h3>说明:</h3>
     * <blockquote>
     * <ol>
     * <li>返回的map是 提取参数 <code>arrayValueMap</code>的key做为key,value数组的第一个元素做<code>value</code></li>
     * <li>返回的是 {@link LinkedHashMap},保证顺序和参数 <code>arrayValueMap</code>顺序相同</li>
     * <li>和该方法正好相反的是 {@link #toArrayValueMap(Map)}</li>
     * </ol>
     * </blockquote>
     * 
     * <h3>示例1:</h3>
     * <blockquote>
     * 
     * <pre class="code">
     * Map{@code <String, String[]>} arrayValueMap = new LinkedHashMap{@code <String, String[]>}();
     * 
     * arrayValueMap.put("province", new String[] { "江苏省" });
     * arrayValueMap.put("city", new String[] { "南通市" });
     * LOGGER.info(JsonUtil.format(ParamUtil.toSingleValueMap(arrayValueMap)));
     * </pre>
     * 
     * 返回:
     * 
     * <pre class="code">
     * {
     * "province": "江苏省",
     * "city": "南通市"
     * }
     * </pre>
     * 
     * </blockquote>
     * 
     * <p>
     * 如果arrayValueMap其中有key的值是多值的数组,那么转换到新的map中的时候,value取第一个值,
     * </p>
     * 
     * <h3>示例2:</h3>
     * <blockquote>
     * 
     * <pre class="code">
     * Map{@code <String, String[]>} arrayValueMap = new LinkedHashMap{@code <String, String[]>}();
     * 
     * arrayValueMap.put("province", new String[] { "浙江省", "江苏省" });
     * arrayValueMap.put("city", new String[] { "南通市" });
     * LOGGER.info(JsonUtil.format(ParamUtil.toSingleValueMap(arrayValueMap)));
     * </pre>
     * 
     * 返回:
     * 
     * <pre class="code">
     * {
     * "province": "浙江省",
     * "city": "南通市"
     * }
     * </pre>
     * 
     * </blockquote>
     *
     * @param <K>
     *            the key type
     * @param <V>
     *            the value type
     * @param arrayValueMap
     *            the array value map
     * @return 如果<code>arrayValueMap</code>是null或者empty,那么返回 {@link Collections#emptyMap()},<br>
     *         如果<code>arrayValueMap</code>其中有key的值是多值的数组,那么转换到新的map中的时候,value取第一个值,<br>
     *         如果<code>arrayValueMap</code>其中有key的value是null,那么转换到新的map中的时候,value以 null替代
     * @since 1.8.0 change type to generics
     */
    public static <K, V> Map<K, V> toSingleValueMap(Map<K, V[]> arrayValueMap){
        if (isNullOrEmpty(arrayValueMap)){
            return Collections.emptyMap();
        }
        Map<K, V> singleValueMap = newLinkedHashMap(arrayValueMap.size());//保证顺序和 参数 arrayValueMap 顺序相同
        for (Map.Entry<K, V[]> entry : arrayValueMap.entrySet()){
            singleValueMap.put(entry.getKey(), null == entry.getValue() ? null : entry.getValue()[0]);
        }
        return singleValueMap;
    }

    /**
     * 将单值的参数map转成多值的参数map.
     * 
     * <p style="color:green">
     * 返回的是 {@link LinkedHashMap},保证顺序和 参数 <code>singleValueMap</code>顺序相同
     * </p>
     * 
     * <p>
     * 和该方法正好相反的是 {@link #toSingleValueMap(Map)}
     * </p>
     * 
     * <h3>示例:</h3>
     * <blockquote>
     * 
     * <pre class="code">
     * Map{@code <String, String>} singleValueMap = new LinkedHashMap{@code <String, String>}();
     * 
     * singleValueMap.put("province", "江苏省");
     * singleValueMap.put("city", "南通市");
     * 
     * LOGGER.info(JsonUtil.format(ParamUtil.toArrayValueMap(singleValueMap)));
     * </pre>
     * 
     * 返回:
     * 
     * <pre class="code">
     * {
     * "province": ["江苏省"],
     * "city": ["南通市"]
     * }
     * </pre>
     * 
     * </blockquote>
     *
     * @param <K>
     *            the key type
     * @param singleValueMap
     *            the name and value map
     * @return 如果参数 <code>singleValueMap</code> 是null或者empty,那么返回 {@link Collections#emptyMap()}<br>
     *         否则迭代 <code>singleValueMap</code> 将value转成数组,返回新的 <code>arrayValueMap</code>
     * @since 1.6.2
     */
    public static <K> Map<K, String[]> toArrayValueMap(Map<K, String> singleValueMap){
        if (isNullOrEmpty(singleValueMap)){
            return Collections.emptyMap();
        }
        Map<K, String[]> arrayValueMap = newLinkedHashMap(singleValueMap.size());//保证顺序和参数singleValueMap顺序相同
        for (Map.Entry<K, String> entry : singleValueMap.entrySet()){
            arrayValueMap.put(entry.getKey(), toArray(entry.getValue()));//注意此处的Value不要声明成V,否则会变成Object数组
        }
        return arrayValueMap;
    }

    /**
     * 仅当 <code>null != map 并且 null != value</code>才将key/value put到map中.
     * 
     * <p>
     * 如果 <code>map</code> 是null,什么都不做<br>
     * 如果 <code>value</code> 是null,也什么都不做<br>
     * 如果 <code>key</code> 是null,依照<code>map</code>的<code>key</code>是否允许是null的 规则<br>
     * </p>
     *
     * @param <K>
     *            the key type
     * @param <V>
     *            the value type
     * @param map
     *            the map to add to
     * @param key
     *            the key
     * @param value
     *            the value
     * @see org.apache.commons.collections4.MapUtils#safeAddToMap(Map, Object, Object)
     * @since 1.4.0
     */
    public static <K, V> void putIfValueNotNull(final Map<K, V> map,final K key,final V value){
        if (null != map && null != value){
            map.put(key, value);
        }
    }

    /**
     * Put all if not null.
     *
     * @param <K>
     *            the key type
     * @param <V>
     *            the value type
     * @param map
     *            the map
     * @param m
     *            mappings to be stored in this map
     * @see java.util.Map#putAll(Map)
     * @since 1.6.3
     */
    public static <K, V> void putAllIfNotNull(final Map<K, V> map,Map<? extends K, ? extends V> m){
        if (null != map && null != m){
            map.putAll(m);// m 如果是null 会报错
        }
    }

    /**
     * 仅当 <code>null != map 并且 isNotNullOrEmpty(value)</code>才将key/value put到map中.
     * 
     * <p>
     * 如果 <code>map</code> 是null,什么都不做<br>
     * 如果 <code>value</code> 是null或者empty,也什么都不做<br>
     * 如果 <code>key</code> 是null,依照<code>map</code>的<code>key</code>是否允许是null的 规则<br>
     * </p>
     *
     * <p>
     * 如果你有以下代码,
     * </p>
     * 
     * <blockquote>
     * 
     * <pre class="code">
     * 
     * if (isNotNullOrEmpty(taoBaoOAuthLoginForCodeEntity.getState())){
     *     nameAndValueMap.put("state", taoBaoOAuthLoginForCodeEntity.getState());
     * }
     * 
     * </pre>
     * 
     * 此时可以重构成:
     * 
     * <pre class="code">
     * MapUtil.putIfValueNotNullOrEmpty(nameAndValueMap, "state", taoBaoOAuthLoginForCodeEntity.getState());
     * </pre>
     * 
     * </blockquote>
     *
     * @param <K>
     *            the key type
     * @param <V>
     *            the value type
     * @param map
     *            the map
     * @param key
     *            the key
     * @param value
     *            the value
     * @since 1.6.3
     */
    public static <K, V> void putIfValueNotNullOrEmpty(final Map<K, V> map,final K key,final V value){
        if (null != map && isNotNullOrEmpty(value)){
            map.put(key, value);
        }
    }

    /**
     * 如果<code>map</code>中存在<code>key</code>,那么累加<code>value</code>值;如果不存在那么直接put.
     * 
     * <h3>示例:</h3>
     * 
     * <blockquote>
     * 
     * <pre class="code">
     * 
     * Map{@code <String, Integer>} map = new HashMap{@code <String, Integer>}();
     * MapUtil.putSumValue(map, "1000001", 5);
     * MapUtil.putSumValue(map, "1000002", 5);
     * MapUtil.putSumValue(map, "1000002", 5);
     * LOGGER.debug(JsonUtil.format(map));
     * 
     * </pre>
     * 
     * 返回:
     * 
     * <pre class="code">
     * {
     * "1000001": 5,
     * "1000002": 10
     * }
     * </pre>
     * 
     * </blockquote>
     * 
     * 该方法特别适合于以下的方法:
     * 
     * <blockquote>
     * 
     * <pre class="code">
     * 
     * if (disadvantageMap.containsKey(disadvantageToken)){
     *     disadvantageMap.put(disadvantageToken, disadvantageMap.get(disadvantageToken) + 1);
     * }else{
     *     disadvantageMap.put(disadvantageToken, 1);
     * }
     * 
     * </pre>
     * 
     * 此时可以重构成:
     * 
     * <pre class="code">
     * MapUtil.putSumValue(disadvantageMap, disadvantageToken, 1);
     * </pre>
     * 
     * </blockquote>
     * 
     * @param <K>
     *            the key type
     * @param map
     *            the map
     * @param key
     *            the key
     * @param value
     *            the value
     * @return 如果 <code>map</code> 是null,抛出 {@link NullPointerException}<br>
     *         如果 <code>value</code> 是null,抛出 {@link NullPointerException}<br>
     * @see org.apache.commons.collections4.bag.HashBag
     * @see org.apache.commons.lang3.mutable.MutableInt
     * @see "java.util.Map#getOrDefault(Object, Object)"
     * @see <a href="http://stackoverflow.com/questions/81346/most-efficient-way-to-increment-a-map-value-in-java">most-efficient-way-to-
     *      increment-a-map-value-in-java</a>
     * @since 1.5.5
     */
    public static <K> Map<K, Integer> putSumValue(Map<K, Integer> map,K key,Integer value){
        Validate.notNull(map, "map can't be null!");
        Validate.notNull(value, "value can't be null!");

        Integer v = map.get(key);//这里不要使用 map.containsKey(key),否则会有2次  two potentially expensive operations
        map.put(key, null == v ? value : value + v);//Suggestion: you should care about code readability more than little performance gain in most of the time.
        return map;
    }

    /**
     * 往 map 中put 指定 key value(多值形式).
     * 
     * <h3>说明:</h3>
     * <blockquote>
     * <ol>
     * <li>map已经存在相同名称的key,那么value以list的形式累加.</li>
     * <li>如果map中不存在指定名称的key,那么会构建一个ArrayList</li>
     * </ol>
     * </blockquote>
     * 
     * <h3>示例:</h3>
     * 
     * <blockquote>
     * 
     * <pre class="code">
     * 
     * Map{@code <String, List<String>>} mutiMap = newLinkedHashMap(2);
     * MapUtil.putMultiValue(mutiMap, "name", "张飞");
     * MapUtil.putMultiValue(mutiMap, "name", "关羽");
     * MapUtil.putMultiValue(mutiMap, "age", "30");
     * 
     * LOGGER.debug(JsonUtil.format(mutiMap));
     * 
     * </pre>
     * 
     * 返回:
     * 
     * <pre class="code">
    {
            "name":         [
                "张飞",
                "关羽"
            ],
            "age": ["30"]
        }
     * 
     * </pre>
     * 
     * </blockquote>
     * 
     * <h3>对于下面的代码:</h3>
     * 
     * <blockquote>
     * 
     * <pre class="code">
     * 
     * private void putItemToMap(Map{@code <String, List<Item>>} map,String tagName,Item item){
     *     List{@code <Item>} itemList = map.get(tagName);
     * 
     *     if (isNullOrEmpty(itemList)){
     *         itemList = new ArrayList{@code <Item>}();
     *     }
     *     itemList.add(item);
     *     map.put(tagName, itemList);
     * }
     * 
     * </pre>
     * 
     * 可以重构成:
     * 
     * <pre class="code">
     * 
     * private void putItemToMap(Map{@code <String, List<Item>>} map,String tagName,Item item){
     *     com.feilong.core.util.MapUtil.putMultiValue(map, tagName, item);
     * }
     * </pre>
     * 
     * </blockquote>
     *
     * @param <K>
     *            the key type
     * @param <V>
     *            the value type
     * @param map
     *            the map
     * @param key
     *            the key
     * @param value
     *            the value
     * @return 如果 <code>map</code> 是null,抛出 {@link NullPointerException}<br>
     * @see "com.google.common.collect.ArrayListMultimap"
     * @see org.apache.commons.collections4.MultiValuedMap
     * @see org.apache.commons.collections4.IterableMap
     * @see org.apache.commons.collections4.MultiMapUtils
     * @see org.apache.commons.collections4.multimap.AbstractMultiValuedMap#put(Object, Object)
     * @since 1.6.2
     */
    public static <K, V> Map<K, List<V>> putMultiValue(Map<K, List<V>> map,K key,V value){
        Validate.notNull(map, "map can't be null!");

        List<V> list = ObjectUtils.defaultIfNull(map.get(key), new ArrayList<V>());
        list.add(value);

        map.put(key, list);
        return map;
    }

    /**
     * 获得一个<code>map</code> 中的按照指定的<code>key</code> 整理成新的map.
     * 
     * <p>
     * 注意:如果循环的 key不在map key里面,则返回的map中忽略该key,并输出warn level log
     * </p>
     * 
     * <p>
     * 返回的map为 {@link LinkedHashMap},key的顺序 按照参数 <code>keys</code>的顺序
     * </p>
     * 
     * <h3>示例:</h3>
     * <blockquote>
     * 
     * <pre class="code">
     * Map{@code <String, Integer>} map = new HashMap{@code <String, Integer>}();
     * map.put("a", 3007);
     * map.put("b", 3001);
     * map.put("c", 3001);
     * map.put("d", 3003);
     * LOGGER.debug(JsonUtil.format(MapUtil.getSubMap(map, "a", "c")));
     * </pre>
     * 
     * 返回:
     * 
     * <pre class="code">
     * {
     * "a": 3007,
     * "c": 3001
     * }
     * </pre>
     * 
     * </blockquote>
     *
     * @param <K>
     *            the key type
     * @param <T>
     *            the generic type
     * @param map
     *            the map
     * @param keys
     *            如果循环的 key不在map key里面,则返回的map中忽略该key,并输出warn level log
     * @return 如果 <code>map</code> 是null或者empty,返回 {@link Collections#emptyMap()};<br>
     *         如果 <code>keys</code> 是null或者empty,直接返回 <code>map</code><br>
     *         如果循环的 key不在map key里面,则返回的map中忽略该key,并输出warn level log
     */
    @SafeVarargs
    public static <K, T> Map<K, T> getSubMap(Map<K, T> map,K...keys){
        if (isNullOrEmpty(map)){
            return Collections.emptyMap();
        }
        if (isNullOrEmpty(keys)){
            return map;
        }
        //保证元素的顺序 ,key的顺序 按照参数 <code>keys</code>的顺序
        Map<K, T> returnMap = newLinkedHashMap(keys.length);
        for (K key : keys){
            if (map.containsKey(key)){
                returnMap.put(key, map.get(key));
            }else{
                LOGGER.warn("map:[{}] don't contains key:[{}]", JsonUtil.format(map.keySet(), 0, 0), key);
            }
        }
        return returnMap;
    }

    /**
     * 获得 sub map(去除不需要的keys).
     * 
     * <p>
     * 返回值为 {@link LinkedHashMap},key的顺序 按照参数 <code>map</code>的顺序
     * </p>
     * 
     * <h3>示例:</h3>
     * <blockquote>
     * 
     * <pre class="code">
     * Map{@code <String, Integer>} map = new LinkedHashMap{@code <String, Integer>}();
     * 
     * map.put("a", 3007);
     * map.put("b", 3001);
     * map.put("c", 3002);
     * map.put("g", -1005);
     * 
     * LOGGER.debug(JsonUtil.format(MapUtil.getSubMapExcludeKeys(map, "a", "g", "m")));
     * </pre>
     * 
     * 返回:
     * 
     * <pre class="code">
     * {
     * "b": 3001,
     * "c": 3002
     * }
     * 
     * </pre>
     * 
     * </blockquote>
     * 
     * <p>
     * 如果 <code>excludeKeys</code>中含有 map 中不存在的key,将会输出warn级别的log
     * </p>
     * 
     * @param <K>
     *            the key type
     * @param <T>
     *            the generic type
     * @param map
     *            the map
     * @param excludeKeys
     *            the keys
     * @return 如果 <code>map</code> 是null或者empty,返回 {@link Collections#emptyMap()};<br>
     *         如果 <code>excludeKeys</code> 是null或者empty,直接返回 <code>map</code>
     * @since 1.0.9
     */
    @SafeVarargs
    public static <K, T> Map<K, T> getSubMapExcludeKeys(Map<K, T> map,K...excludeKeys){
        if (isNullOrEmpty(map)){
            return Collections.emptyMap();
        }
        return isNullOrEmpty(excludeKeys) ? map : removeKeys(new LinkedHashMap<K, T>(map), excludeKeys); //保证元素的顺序 
    }

    /**
     * 删除 <code>map</code> 的指定的 <code>keys</code>.
     * 
     * <h3>注意</h3>
     * 
     * <blockquote>
     * <p>
     * 直接操作的是参数<code>map</code>,迭代 <code>keys</code>,<br>
     * 如果 <code>map</code>包含key,那么直接调用 {@link Map#remove(Object)},<br>
     * 如果不包含,那么输出warn级别日志
     * </p>
     * </blockquote>
     * 
     * 
     * <h3>示例:</h3>
     * 
     * <blockquote>
     * 
     * <pre class="code">
     * 
     * Map{@code <String, String>} map = newLinkedHashMap(3);
     * 
     * map.put("name", "feilong");
     * map.put("age", "18");
     * map.put("country", "china");
     * 
     * LOGGER.debug(JsonUtil.format(MapUtil.removeKeys(map, "country")));
     * 
     * </pre>
     * 
     * 返回:
     * 
     * <pre class="code">
     * {
     * "name": "feilong",
     * "age": "18"
     * }
     * 
     * </pre>
     * 
     * </blockquote>
     *
     * @param <K>
     *            the key type
     * @param <V>
     *            the value type
     * @param map
     *            the map
     * @param keys
     *            the keys
     * @return 如果 <code>map</code> 是null,抛出 {@link NullPointerException}<br>
     * @since 1.6.3
     */
    @SafeVarargs
    public static <K, V> Map<K, V> removeKeys(Map<K, V> map,K...keys){
        Validate.notNull(map, "map can't be null!");
        for (K key : keys){
            if (map.containsKey(key)){
                map.remove(key);
            }else{
                LOGGER.warn("map:[{}] don't contains key:[{}]", JsonUtil.format(map.keySet(), 0, 0), key);
            }
        }
        return map;
    }

    /**
     * 将 <code>map</code> 的key和value互转.
     * 
     * <p>
     * <span style="color:red">这个操作map预先良好的定义</span>.<br>
     * 如果传过来的map,不同的key有相同的value,那么返回的map(key)只会有一个(value),其他重复的key被丢掉了
     * </p>
     * 
     * <h3>示例:</h3>
     * <blockquote>
     * 
     * <pre class="code">
     * Map{@code <String, Integer>} map = new HashMap{@code <String, Integer>}();
     * map.put("a", 3007);
     * map.put("b", 3001);
     * map.put("c", 3001);
     * map.put("d", 3003);
     * LOGGER.debug(JsonUtil.format(MapUtil.invertMap(map)));
     * </pre>
     * 
     * 返回:
     * 
     * <pre class="code">
     * {
     * "3001": "c",
     * "3007": "a",
     * "3003": "d"
     * }
     * </pre>
     * 
     * 可以看出 b元素被覆盖了
     * 
     * </blockquote>
     * 
     * @param <K>
     *            the key type
     * @param <V>
     *            the value type
     * @param map
     *            the map
     * @return 如果<code>map</code> 是null,抛出 {@link NullPointerException}<br>
     *         如果<code>map</code> 是empty,返回 一个 new HashMap
     * @see org.apache.commons.collections4.MapUtils#invertMap(Map)
     * @since 1.2.2
     */
    public static <K, V> Map<V, K> invertMap(Map<K, V> map){
        return MapUtils.invertMap(map);//返回的是 HashMap
    }

    /**
     * 以参数 <code>map</code>的key为key,以参数 <code>map</code> value的指定<code>extractPropertyName</code>属性值为值,拼装成新的map返回.
     * 
     * <h3>说明:</h3>
     * <blockquote>
     * <ol>
     * <li>如果在抽取的过程中,<code>map</code>没有某个 <code>includeKeys</code>,将会输出 warn log</li>
     * <li>返回map的顺序,按照参数 map key的顺序</li>
     * </ol>
     * </blockquote>
     * 
     * <h3>示例:</h3>
     * <blockquote>
     * 
     * <pre class="code">
     * Map{@code <Long, User>} map = new LinkedHashMap{@code <Long, User>}();
     * map.put(1L, new User(100L));
     * map.put(2L, new User(200L));
     * map.put(5L, new User(500L));
     * map.put(4L, new User(400L));
     * 
     * LOGGER.debug(JsonUtil.format(MapUtil.extractSubMap(map, "id")));
     * </pre>
     * 
     * 返回:
     * 
     * <pre class="code">
    {
        "1": 100,
        "2": 200,
        "5": 500,
        "4": 400
    }
     * </pre>
     * 
     * </blockquote>
     * 
     * @param <K>
     *            the key type
     * @param <O>
     *            the generic type
     * @param <V>
     *            the generic type
     * @param map
     *            the map
     * @param extractPropertyName
     *            泛型O对象指定的属性名称,Possibly indexed and/or nested name of the property to be modified,参见
     *            <a href="../bean/BeanUtil.html#propertyName">propertyName</a>
     * @return 如果 <code>map</code> 是null或者empty,返回 {@link Collections#emptyMap()}<br>
     *         如果 <code>extractPropertyName</code> 是null,抛出 {@link NullPointerException}<br>
     *         如果 <code>extractPropertyName</code> 是blank,抛出 {@link IllegalArgumentException}<br>
     * @since 1.8.0 remove class param
     */
    public static <K, O, V> Map<K, V> extractSubMap(Map<K, O> map,String extractPropertyName){
        return extractSubMap(map, null, extractPropertyName);
    }

    /**
     * 以参数 <code>map</code>的key为key,以参数 <code>map</code>value的指定<code>extractPropertyName</code>
     * 属性值为值,拼装成新的map返回.
     * 
     * <h3>说明:</h3>
     * <blockquote>
     * <ol>
     * <li>key需要在 <code>includeKeys</code>范围内</li>
     * <li>如果在抽取的过程中,<code>map</code>没有某个 <code>includeKeys</code>,将会输出 warn log</li>
     * <li>如果参数 <code>includeKeys</code>是null,那么会抽取map所有的key</li>
     * <li>返回map的顺序,按照参数includeKeys的顺序(如果includeKeys是null,那么按照map key的顺序)</li>
     * </ol>
     * </blockquote>
     * 
     * <h3>示例:</h3>
     * <blockquote>
     * 
     * <pre class="code">
     * Map{@code <Long, User>} map = new LinkedHashMap{@code <Long, User>}();
     * map.put(1L, new User(100L));
     * map.put(2L, new User(200L));
     * map.put(53L, new User(300L));
     * map.put(5L, new User(500L));
     * map.put(6L, new User(600L));
     * map.put(4L, new User(400L));
     * 
     * Long[] includeKeys = { 5L, 4L };
     * LOGGER.debug(JsonUtil.format(MapUtil.extractSubMap(map, includeKeys, "id")));
     * </pre>
     * 
     * 返回:
     * 
     * <pre class="code">
     * {
     * "5": 500,
     * "4": 400
     * }
     * </pre>
     * 
     * </blockquote>
     * 
     * <h3>典型示例:</h3>
     * 
     * <blockquote>
     * 
     * <pre class="code">
     * 
     * private Map{@code <Long, Long>} constructPropertyIdAndItemPropertiesIdMap(
     *                 String properties,
     *                 Map{@code <Long, PropertyValueSubViewCommand>} itemPropertiesIdAndPropertyValueSubViewCommandMap){
     *     Long[] itemPropertiesIds = StoCommonUtil.toItemPropertiesIdLongs(properties);
     * 
     *     Map{@code <Long, Long>} itemPropertiesIdAndPropertyIdMap = MapUtil
     *                     .extractSubMap(itemPropertiesIdAndPropertyValueSubViewCommandMap, itemPropertiesIds, "propertyId");
     * 
     *     return MapUtil.invertMap(itemPropertiesIdAndPropertyIdMap);
     * }
     * 
     * </pre>
     * 
     * </blockquote>
     *
     * @param <K>
     *            the key type
     * @param <O>
     *            the generic type
     * @param <V>
     *            the value type
     * @param map
     *            the map
     * @param includeKeys
     *            the include keys
     * @param extractPropertyName
     *            泛型O对象指定的属性名称,Possibly indexed and/or nested name of the property to be modified,参见
     *            <a href="../bean/BeanUtil.html#propertyName">propertyName</a>
     * @return 如果 <code>map</code> 是null或者empty,返回 {@link Collections#emptyMap()}<br>
     *         如果 <code>extractPropertyName</code> 是null,抛出 {@link NullPointerException}<br>
     *         如果 <code>extractPropertyName</code> 是blank,抛出 {@link IllegalArgumentException}<br>
     *         如果 <code>includeKeys</code> 是null或者empty, then will extract map total keys<br>
     * @since 1.8.0 remove class param
     */
    public static <K, O, V> Map<K, V> extractSubMap(Map<K, O> map,K[] includeKeys,String extractPropertyName){
        if (isNullOrEmpty(map)){
            return Collections.emptyMap();
        }

        Validate.notBlank(extractPropertyName, "extractPropertyName can't be null/empty!");

        //如果excludeKeys是null,那么抽取所有的key
        @SuppressWarnings("unchecked") // NOPMD - false positive for generics
        K[] useIncludeKeys = isNullOrEmpty(includeKeys) ? (K[]) map.keySet().toArray() : includeKeys;

        //保证元素的顺序,顺序是参数  includeKeys的顺序
        Map<K, V> returnMap = newLinkedHashMap(useIncludeKeys.length);
        for (K key : useIncludeKeys){
            if (map.containsKey(key)){
                returnMap.put(key, PropertyUtil.<V> getProperty(map.get(key), extractPropertyName));
            }else{
                LOGGER.warn("map:[{}] don't contains key:[{}]", JsonUtil.format(map.keySet(), 0, 0), key);
            }
        }
        return returnMap;
    }

    //*************************************************************************************************

    /**
     * Creates a {@code HashMap} instance, with a high enough "initial capacity" that it <i>should</i> hold {@code expectedSize} elements
     * without growth.
     * This behavior cannot be broadly guaranteed, but it is observed to be true for OpenJDK 1.7. It also can't be guaranteed that the
     * method isn't inadvertently <i>oversizing</i> the returned map.
     * 
     * <h3>以前你可能需要这么写代码:</h3>
     * <blockquote>
     * 
     * <pre class="code">
     * Map{@code <String, Map<Long, List<String>>>} map = new HashMap{@code <String, Map<Long, List<String>>>}(16);
     * </pre>
     * 
     * 如果你是使用JDK1.7或者以上,你可以使用 钻石符:
     * 
     * <pre class="code">
     * Map{@code <String, Map<Long, List<String>>>} map = new HashMap{@code <>}(16);
     * </pre>
     * 
     * 不过只要你是使用1.5+,你都可以写成:
     * 
     * <pre class="code">
     * Map{@code <String, Map<Long, List<String>>>} map = MapUtil.newHashMap(16);
     * </pre>
     * 
     * </blockquote>
     *
     * @param <K>
     *            the key type
     * @param <V>
     *            the value type
     * @param expectedSize
     *            the number of entries you expect to add to the returned map
     * @return a new, empty {@code HashMap} with enough capacity to hold {@code expectedSize} entries without resizing
     * @see "com.google.common.collect.Maps#newHashMapWithExpectedSize(int)"
     * @see java.util.HashMap#HashMap(int)
     * @since 1.7.1
     */
    public static <K, V> HashMap<K, V> newHashMap(int expectedSize){
        return new HashMap<K, V>(toInitialCapacity(expectedSize));
    }

    /**
     * Creates a {@code LinkedHashMap} instance, with a high enough "initial capacity" that it <i>should</i> hold {@code expectedSize}
     * elements without growth. This behavior cannot be broadly guaranteed, but it is observed to be true for OpenJDK 1.7. <br>
     * It also can't be guaranteed that the method isn't inadvertently <i>oversizing</i> the returned map.
     *
     * @param <K>
     *            the key type
     * @param <V>
     *            the value type
     * @param expectedSize
     *            the number of entries you expect to add to the returned map
     * @return a new, empty {@code LinkedHashMap} with enough capacity to hold {@code expectedSize} entries without resizing
     * @see "com.google.common.collect.Maps#newLinkedHashMapWithExpectedSize(int)"
     * @see java.util.LinkedHashMap#LinkedHashMap(int)
     * @since 1.7.1
     */
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(int expectedSize){
        return new LinkedHashMap<K, V>(toInitialCapacity(expectedSize));
    }

    /**
     * 将<code>size</code>转成 <code>initialCapacity</code> (for {@link java.util.HashMap}).
     * 
     * <p>
     * 适合于明确知道 hashmap size,现在需要初始化的情况
     * </p>
     *
     * @param size
     *            map的 size
     * @return the int
     * @see <a href="http://www.iteye.com/topic/1134016">java hashmap,如果确定只装载100个元素,new HashMap(?)多少是最佳的,why？ </a>
     * @see <a href=
     *      "http://stackoverflow.com/questions/30220820/difference-between-new-hashmapint-and-guava-maps-newhashmapwithexpectedsizein">
     *      Difference between new HashMap(int) and guava Maps.newHashMapWithExpectedSize(int)</a>
     * @see <a href="http://stackoverflow.com/questions/15844035/best-hashmap-initial-capacity-while-indexing-a-list">Best HashMap initial
     *      capacity while indexing a List</a>
     * @see java.util.HashMap#HashMap(Map)
     * @see "com.google.common.collect.Maps#capacity(int)"
     * @see java.util.HashMap#inflateTable(int)
     * @see org.apache.commons.collections4.map.AbstractHashedMap#calculateNewCapacity(int)
     * @since 1.7.1
     */
    private static int toInitialCapacity(int size){
        Validate.isTrue(size >= 0, "size :[%s] must >=0", size);

        //借鉴了 google guava 的实现,不过 guava 不同版本实现不同
        //guava 19 (int) (expectedSize / 0.75F + 1.0F)
        //guava 18  expectedSize + expectedSize / 3
        //google-collections 1.0  Math.max(expectedSize * 2, 16)

        //This is the calculation used in JDK8 to resize when a putAll happens it seems to be the most conservative calculation we can make.  
        return (int) (size / 0.75f) + 1;//0.75 is the default load factor
    }

}
