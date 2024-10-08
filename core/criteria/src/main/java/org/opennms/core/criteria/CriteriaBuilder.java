/*
 * Licensed to The OpenNMS Group, Inc (TOG) under one or more
 * contributor license agreements.  See the LICENSE.md file
 * distributed with this work for additional information
 * regarding copyright ownership.
 *
 * TOG licenses this file to You under the GNU Affero General
 * Public License Version 3 (the "License") or (at your option)
 * any later version.  You may not use this file except in
 * compliance with the License.  You may obtain a copy of the
 * License at:
 *
 *      https://www.gnu.org/licenses/agpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.  See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.opennms.core.criteria;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.opennms.core.criteria.Alias.JoinType;
import org.opennms.core.criteria.Fetch.FetchType;
import org.opennms.core.criteria.restrictions.AllRestriction;
import org.opennms.core.criteria.restrictions.Restriction;
import org.opennms.core.criteria.restrictions.Restrictions;
import org.opennms.core.criteria.restrictions.SqlRestriction.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CriteriaBuilder {
	
	private static final Logger LOG = LoggerFactory.getLogger(CriteriaBuilder.class);
	
    private Class<?> m_class;

    private String m_rootAlias;

    private OrderBuilder m_orderBuilder = new OrderBuilder();

    private Set<Fetch> m_fetch = new LinkedHashSet<>();

    private AliasBuilder m_aliasBuilder = new AliasBuilder();

    private boolean m_distinct = false;

    private Set<Restriction> m_restrictions = new LinkedHashSet<>();

    private boolean m_negateNext = false;

    private Integer m_limit = null;

    private Integer m_offset = null;

    private String m_matchType = "all";

    private boolean m_isMultipleAnd = false;

    private static final Restriction[] EMPTY_RESTRICTION_ARRAY = new Restriction[0];

    public CriteriaBuilder(final Class<?> clazz) {
        this(clazz, null);
    }

    public CriteriaBuilder(final Class<?> clazz, final String rootAlias) {
        m_class = clazz;
        m_rootAlias = rootAlias;
    }

    public Criteria toCriteria() {
        final Criteria criteria = new Criteria(m_class, m_rootAlias);
        criteria.setOrders(m_orderBuilder.getOrderCollection());
        criteria.setAliases(m_aliasBuilder.getAliasCollection());
        criteria.setFetchTypes(m_fetch);
        criteria.setDistinct(m_distinct);
        criteria.setLimit(m_limit);
        criteria.setOffset(m_offset);
        criteria.setMultipleAnd(m_isMultipleAnd);
        if ("any".equals(m_matchType)) {
            criteria.setRestrictions(Collections.singleton(Restrictions.any(m_restrictions.toArray(EMPTY_RESTRICTION_ARRAY))));
        } else {
            criteria.setRestrictions(m_restrictions);
        }
        if(isNestedMultipleAnd(m_restrictions)){
            throw new IllegalArgumentException("Use of nested 'multiAnd' is not allowed");
        }
        return criteria;
    }
    private boolean isNestedMultipleAnd(Collection<? extends Restriction> allRestrictions){
        //set of multiand allRestrictions
        Set<Restriction> multiAndRestrictionSet = allRestrictions.stream().filter(
                restriction -> restriction.getType().equals(Restriction.RestrictionType.MULTIAND)).collect(Collectors.toSet());
        //Get all inner restrictions
        Set<Restriction> allInnerRestrictions = multiAndRestrictionSet.stream().flatMap(restriction ->
                ((AllRestriction) restriction).getRestrictions().stream()).collect(Collectors.toSet());
        //check if any "multiAnd" present, then return true
        return allInnerRestrictions.stream().anyMatch(restriction -> restriction.getType().equals(Restriction.RestrictionType.MULTIAND));
    }

    public CriteriaBuilder match(final String type) {
        if ("all".equals(type) || "any".equals(type)) {
            m_matchType = type;
        } else {
            throw new IllegalArgumentException("match type must be 'all' or 'any'");
        }
        return this;
    }

    public CriteriaBuilder fetch(final String attribute) {
        m_fetch.add(new Fetch(attribute));
        return this;
    }

    public CriteriaBuilder fetch(final String attribute, final FetchType type) {
        m_fetch.add(new Fetch(attribute, type));
        return this;
    }

    public CriteriaBuilder join(final String associationPath, final String alias) {
        return alias(associationPath, alias, JoinType.LEFT_JOIN);
    }

    public CriteriaBuilder alias(final Alias alias) {
        m_aliasBuilder.alias(alias);
        return this;
    }

    public CriteriaBuilder alias(final String associationPath, final String alias) {
        return alias(associationPath, alias, JoinType.LEFT_JOIN);
    }

    public CriteriaBuilder createAlias(final String associationPath, final String alias) {
        return alias(associationPath, alias);
    }

    public CriteriaBuilder join(final String associationPath, final String alias, final JoinType type) {
        return alias(associationPath, alias, type);
    }

    public CriteriaBuilder alias(final String associationPath, final String alias, final JoinType type) {
        return alias(associationPath, alias, type, null);
    }

    public CriteriaBuilder alias(final String associationPath, final String alias, final JoinType type, final Restriction joinCondition) {
        m_aliasBuilder.alias(associationPath, alias, type, joinCondition);
        return this;
    }

    public CriteriaBuilder limit(final Integer limit) {
        m_limit = ((limit == null || limit == 0) ? null : limit);
        return this;
    }

    public CriteriaBuilder offset(final Integer offset) {
        m_offset = ((offset == null || offset == 0) ? null : offset);
        return this;
    }

    public CriteriaBuilder clearOrder() {
        m_orderBuilder.clear();
        return this;
    }

    public CriteriaBuilder orderBy(final String attribute) {
        return orderBy(attribute, true);
    }

    public CriteriaBuilder orderBy(final String attribute, final boolean ascending) {
        m_orderBuilder.append(new Order(attribute, ascending));
        return this;
    }

    public CriteriaBuilder asc() {
        m_orderBuilder.asc();
        return this;
    }

    public CriteriaBuilder desc() {
        m_orderBuilder.desc();
        return this;
    }

    public CriteriaBuilder distinct() {
        m_distinct = true;
        return this;
    }

    public CriteriaBuilder count() {
        m_orderBuilder.clear();
        m_limit = null;
        m_offset = null;
        return this;
    }

    public CriteriaBuilder distinct(final boolean isDistinct) {
        m_distinct = isDistinct;
        return this;
    }

    protected boolean addRestriction(final Restriction restriction) {
        if (m_negateNext) {
            m_negateNext = false;
            return m_restrictions.add(Restrictions.not(restriction));
        } else {
            return m_restrictions.add(restriction);
        }
    }

    public CriteriaBuilder isNull(final String attribute) {
        addRestriction(Restrictions.isNull(attribute));
        return this;
    }

    public CriteriaBuilder isNotNull(final String attribute) {
        addRestriction(Restrictions.isNotNull(attribute));
        return this;
    }

    public CriteriaBuilder id(final Integer id) {
        addRestriction(Restrictions.id(id));
        return this;
    }

    public CriteriaBuilder eq(final String attribute, final Object comparator) {
        addRestriction(Restrictions.eq(attribute, comparator));
        return this;
    }

    public CriteriaBuilder ne(final String attribute, final Object comparator) {
        if (m_negateNext) {
            m_negateNext = false;
            addRestriction(Restrictions.eq(attribute, comparator));
        } else {
            addRestriction(Restrictions.not(Restrictions.eq(attribute, comparator)));
        }
        return this;
    }

    public CriteriaBuilder gt(final String attribute, final Object comparator) {
        addRestriction(Restrictions.gt(attribute, comparator));
        return this;
    }

    public CriteriaBuilder ge(final String attribute, final Object comparator) {
        addRestriction(Restrictions.ge(attribute, comparator));
        return this;
    }

    public CriteriaBuilder lt(final String attribute, final Object comparator) {
        addRestriction(Restrictions.lt(attribute, comparator));
        return this;
    }

    public CriteriaBuilder le(final String attribute, final Object comparator) {
        addRestriction(Restrictions.le(attribute, comparator));
        return this;
    }

    public CriteriaBuilder like(final String attribute, final Object comparator) {
        addRestriction(Restrictions.like(attribute, comparator));
        return this;
    }

    public CriteriaBuilder ilike(final String attribute, final Object comparator) {
        addRestriction(Restrictions.ilike(attribute, comparator));
        return this;
    }

    public CriteriaBuilder iplike(final String attribute, final Object comparator) {
        addRestriction(Restrictions.iplike(attribute, comparator));
        return this;
    }

    public CriteriaBuilder contains(final String attribute, final Object comparator) {
        addRestriction(Restrictions.ilike(attribute, "%" + comparator + "%"));
        return this;
    }

    public CriteriaBuilder in(final String attribute, final Collection<?> collection) {
        addRestriction(Restrictions.in(attribute, collection));
        return this;
    }

    public CriteriaBuilder between(final String attribute, final Object begin, final Object end) {
        addRestriction(Restrictions.between(attribute, begin, end));
        return this;
    }

    public CriteriaBuilder sql(final String sql) {
        addRestriction(Restrictions.sql((String) sql));
        return this;
    }

    public CriteriaBuilder sql(final String sql, final Object parameter, final Type type) {
        addRestriction(Restrictions.sql((String) sql, parameter, type));
        return this;
    }

    public CriteriaBuilder sql(final String sql, final Object[] parameters, final Type[] types) {
        addRestriction(Restrictions.sql((String) sql, parameters, types));
        return this;
    }

    public CriteriaBuilder not() {
        m_negateNext = true;
        return this;
    }

    public CriteriaBuilder and(final Restriction... restrictions) {
        addRestriction(Restrictions.and(restrictions));
        return this;
    }

    public CriteriaBuilder multipleAnd(final Restriction... restrictions) {
        m_isMultipleAnd = true;
        addRestriction(Restrictions.multipleAnd(restrictions));
        return this;
    }

    public CriteriaBuilder or(final Restriction... restrictions) {
        final Restriction restriction = Restrictions.or(restrictions);
        addRestriction(restriction);
        return this;
    }

}
