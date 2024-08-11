package com.example.taskmanager.repository;

import com.example.taskmanager.dao.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT t FROM Task t WHERE t.assignee = :email or t.creator= :email order by t.id desc ")
    Page<Task> findTasksByUsersEmail(@Param("email") String email, Pageable pageable);


}
