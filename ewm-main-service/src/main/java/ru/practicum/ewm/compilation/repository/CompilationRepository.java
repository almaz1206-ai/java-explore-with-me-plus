package ru.practicum.ewm.compilation.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.compilation.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query("SELECT c.id FROM Compilation c WHERE c.pinned = :pinned")
    List<Long> findIdsByPinned(@Param("pinned") Boolean pinned, Pageable pageable);

    @Query("SELECT c.id FROM Compilation c")
    List<Long> findAllIds(Pageable pageable);

    @Query("SELECT DISTINCT c FROM Compilation c LEFT JOIN FETCH c.events WHERE c.id IN :ids")
    List<Compilation> findAllWithEventsByIdIn(@Param("ids") List<Long> ids);
}
