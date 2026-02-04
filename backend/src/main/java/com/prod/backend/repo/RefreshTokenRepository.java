package com.prod.backend.repo;

import com.prod.backend.entity.RefreshTokenEntity;
import com.prod.backend.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    @Query("select rt from RefreshTokenEntity rt join fetch rt.user where rt.token = :token")
    Optional<RefreshTokenEntity> findByTokenWithUser(String token);

    @Transactional
    void deleteByUser(UserEntity user);


}
