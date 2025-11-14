package com.example.my_community.post.domain;

import com.example.my_community.user.domain.Role;
import com.example.my_community.user.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PostTest {
    @PersistenceContext
    EntityManager entityManager;

    @Test
    @Rollback(false)
    void idTest() {
        User user = new User("bruni", "123123", Role.USER);
        Post post = new Post(null, user,"게시글 제목", "게시글 본문", OffsetDateTime.now(), 1);
        entityManager.persist(post);
    }

    @Test
    @Rollback(false)
    void implicitJoinTest() {
        // 3개의 유저 및 게시글 더미 데이터 추가
        for (int i = 1; i <= 3; i++) {
            User user = new User(
                    "tester" + i + "@adapterz.kr",
                    "123aS!" + i,
                    Role.USER
            );
            entityManager.persist(user);

            Post post = new Post(
                    null,
                    user,
                    "test title" + i,
                    "test content" + i,
                    OffsetDateTime.now(),
                    1
            );
            entityManager.persist(post);
        }

        List<String> result = entityManager.createQuery(
                "select p.user.nickname from Post p",
                String.class
        ).getResultList();

        System.out.println("닉네임 목록 = " + result);
    }

    @Test
    @Rollback(false)
    void explicitJoinTest() {
        // 3개의 유저 및 게시글 더미 데이터 추가
        for (int i = 1; i <= 3; i++) {
            User user = new User(
                    "tester" + i,
                    "123aS!" + i,
                    Role.USER
            );
            entityManager.persist(user);

            Post post = new Post(
                    null,
                    user,
                    "test title" + i,
                    "test content" + i,
                    OffsetDateTime.now(),
                    1
            );
            entityManager.persist(post);
        }

        List<Post> result = entityManager.createQuery(
                        "select p from User u join u.posts p where u.nickname = :nickname",
                        Post.class
                ).setParameter("nickname", "tester1")
                .getResultList();

        System.out.println("tester1의 게시글 수(명시적 조인) = " + result.size());
    }
}