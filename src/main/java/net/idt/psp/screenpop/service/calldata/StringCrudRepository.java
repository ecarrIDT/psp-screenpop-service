package net.idt.psp.screenpop.service.calldata;

import java.time.Duration;
import org.springframework.stereotype.Repository;

@Repository
public interface StringCrudRepository {
    // save a record with a ttl (time-to-live)
    public void saveByKey (String key, String value, Duration ttl); 

    // delete a record by key
    public String findByKey (String key);

    // delete a record by key
    public void deleteByKey (String key);

    // whether this repository is available
    public Boolean isAvailable();
}
