package persistence.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import logic.to.AbstractEntityTO;

/**
 * @author riediger
 */
@MappedSuperclass
public abstract class AbstractEntity<E extends AbstractEntity<E, T>, T extends AbstractEntityTO>
		implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4912517575060849948L;
	/**
	 * 
	 */
	private Long id;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
        
	public abstract T createTO();


	public static <ET extends AbstractEntity<ET, TO>, TO extends AbstractEntityTO> List<TO> createTransferList(Iterable<ET> i) {
		List<TO> result = new ArrayList<>();
		for (ET e : i) {
			result.add(e.createTO());
		}
		return result;
	}
}