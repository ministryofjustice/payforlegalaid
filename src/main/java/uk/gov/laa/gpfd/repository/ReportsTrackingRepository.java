package uk.gov.laa.gpfd.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.laa.gpfd.model.ReportsTracking;

@Repository
public interface ReportsTrackingRepository extends JpaRepository<ReportsTracking, UUID> {

}
