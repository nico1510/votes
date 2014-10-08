package persistence;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import persistence.entities.AbstractEntity;
import logic.to.AbstractEntityTO;

public abstract class AbstractAccess<E extends AbstractEntity<E, T>, T extends AbstractEntityTO> {

	protected Class<E> entityClass;

	public AbstractAccess(Class<E> entityClass) {
		this.entityClass = entityClass;
	}

	protected abstract EntityManager getEntityManager();

	public void create(E entity) {
		getEntityManager().persist(entity);
		getEntityManager().flush();
	}

	public E edit(E entity) {
		E result = getEntityManager().merge(entity);
		getEntityManager().flush();
		return result;
	}

	public void remove(E entity) {
		getEntityManager().remove(getEntityManager().merge(entity));
		getEntityManager().flush();
	}

	public E find(Object id) {
		return getEntityManager().find(entityClass, id);
	}

	public List<E> findAll() {
		javax.persistence.criteria.CriteriaQuery<E> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
		cq.select(cq.from(entityClass));
		return getEntityManager().createQuery(cq).getResultList();
	}

	protected E findBy(String fieldName, String value) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<E> cq = cb.createQuery(entityClass);
		Root<E> rt = cq.from(entityClass);
		cq.select(rt).where(cb.equal(rt.get(fieldName), value));
		TypedQuery<E> q = getEntityManager().createQuery(cq);
		try {
			return q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<E> findRange(int[] range) {
		javax.persistence.criteria.CriteriaQuery<E> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
		cq.select(cq.from(entityClass));
		javax.persistence.TypedQuery<E> q = getEntityManager().createQuery(cq);
		q.setMaxResults(range[1] - range[0]);
		q.setFirstResult(range[0]);
		return q.getResultList();
	}

	public int count() {
		javax.persistence.criteria.CriteriaQuery<Long> cq = getEntityManager().getCriteriaBuilder().createQuery(Long.class);
		javax.persistence.criteria.Root<E> rt = cq.from(entityClass);
		cq.select(getEntityManager().getCriteriaBuilder().count(rt));
		return getEntityManager().createQuery(cq).getSingleResult().intValue();
	}
}
