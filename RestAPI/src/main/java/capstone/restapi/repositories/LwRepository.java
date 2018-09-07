package capstone.restapi.repositories;

import capstone.restapi.domain.Lwdata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
This class is an in-memory store of the data, as well as a representation
of the MySql database. It extends the class JpaRepository.
All methods of this class ultimately perform a similar operation on the database.
Objects in the repository and database are of type Lwdata, with a Long type primary key.
 */
@Transactional
public interface LwRepository extends JpaRepository<Lwdata, Long> {

}
