package com.britesnow.samplesocial.dao;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.entity.UserScopedEntity;
import com.google.inject.Inject;
import com.googlecode.gentyref.GenericTypeReflector;

import org.j8ql.query.Query;
import org.j8ql.query.Condition;
import org.j8ql.query.SelectQuery;




import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@SuppressWarnings({"rawtypes", "unchecked"})
public  class BaseDao<E,I> implements IDao<E,I> {

	protected Class<E> entityClass;
	protected Class<I> idClass;

	@Inject
	protected DaoHelper daoHelper;

	protected BaseDao() {
		initEntityClass();
	}

	protected BaseDao(boolean entityClassProvided){
		if (!entityClassProvided){
			initEntityClass();
		}
	}

	private void initEntityClass(){
		if (entityClass == null && idClass == null) {
			Type persistentType = GenericTypeReflector.getTypeParameter(getClass(), BaseDao.class.getTypeParameters()[0]);
			Type persistentIdType = GenericTypeReflector.getTypeParameter(getClass(), BaseDao.class.getTypeParameters()[1]);
			if (persistentType instanceof Class && persistentIdType instanceof Class) {
				this.entityClass = (Class<E>) persistentType;
				this.idClass = (Class<I>) persistentIdType;
			} else {
				throw new IllegalStateException("concrete class " + getClass().getName()
						+ " must have a generic Entity and ID types "
						+ BaseDao.class.getName());
			}
		}

	}




	// --------- IDao Interface --------- //
	@Override
	public Optional<E> get(User user, I id) {
		return daoHelper.first(getSelectQuery().whereId(id));
	}

	@Override
	public I create(User user, E entity) {
		if (entity instanceof UserScopedEntity){
			((UserScopedEntity) entity).setUserId(user.getId());
		}
		return daoHelper.execute(Query.insert(entityClass).value(entity).returningIdAs(idClass));
	}

	@Override
	public I create(User user, Map map){
		if (UserScopedEntity.class.isAssignableFrom(entityClass)) {
			map.put("userId", user.getId());
		}
		return daoHelper.execute(Query.insert(entityClass).value(map).returningIdAs(idClass));
	}

	@Override
	public int update(User user, E entity, I id) {
		return daoHelper.execute(Query.update(entityClass).value(entity).whereId(id));
	}

	@Override
	public int delete(User user, I id) {
		return daoHelper.execute(Query.delete(entityClass).whereId(id));
	}

	@Override
	public List<E> list(User user, Condition filter, int pageIdx, int pageSize, String... orderBy) {
		return daoHelper.list(getListSelectQuery(filter, pageIdx, pageSize, orderBy));
	}

	@Override
	public Long count(Condition filter) {
		return daoHelper.count(Query.select(entityClass).where(filter));
	}

	@Override
	public Class<E> getPersistentClass() {
		// TODO Auto-generated method stub
		return entityClass;
	}
	// --------- IDao Interface --------- //

	// --------- BaseDao Default Implementation --------- //

	/**
	 * This is the default SelectQuery generator, but sometime sub daos might want to do some joins
	 * and therefore override this methods.
	 * @return
	 */
	protected SelectQuery<E> getSelectQuery(){
		return Query.select(entityClass);
	}
	// --------- /BaseDao Default Implementation --------- //

	// --------- Protected --------- //
	/**
	 * Base SelectQuery for the list api. Reused by sub class DAOs that need to add joins to the list.
	 */
	protected SelectQuery<E> getListSelectQuery(Condition filter, int pageIdx, int pageSize, String... orderBy) {
		int limit = pageSize;
		int offset = pageIdx * pageSize;
		// TODO: probably need to add the user.id to the filter condition
		//return Query.select(entityClass).where(filter).limit(pageSize).offset(offset).orderBy(orderBy);
		return getSelectQuery().where(filter).limit(limit).offset(offset).orderBy(orderBy);
	}
	// --------- /Protected --------- //

}