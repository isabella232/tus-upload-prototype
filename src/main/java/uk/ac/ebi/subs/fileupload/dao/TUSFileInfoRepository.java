package uk.ac.ebi.subs.fileupload.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.subs.fileupload.model.TUSFileInfo;

import java.util.List;

@Repository
public interface TUSFileInfoRepository extends JpaRepository<TUSFileInfo, String> {
    List<TUSFileInfo> findByEventType(String eventType);
}
