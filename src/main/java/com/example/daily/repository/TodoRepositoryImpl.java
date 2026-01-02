package com.example.daily.repository;

import com.example.daily.entity.QTag;
import com.example.daily.entity.QTodo;
import com.example.daily.entity.Todo;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.example.daily.entity.QTag.tag;
import static com.example.daily.entity.QTodo.todo;

@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Todo> searchTodos(String title, String tagName, Boolean completed, String username) {
        return queryFactory
                .selectFrom(todo)
                .leftJoin(todo.tags, tag).fetchJoin() // 태그 성능 최적화
                .where(
                        titleContains(title),
                        tagEq(tagName),
                        completedEq(completed),
                        usernameEq(username)
                )
                .fetch();
    }

    @Override
    public Page<Todo> searchTodos(String title, String tagName, Boolean completed, String username, Pageable pageable) {
        // 데이터 조회 (Content)
        List<Todo> content = queryFactory
                .selectFrom(todo)
                .leftJoin(todo.tags, tag).fetchJoin()
                .where(
                        titleContains(title),
                        tagEq(tagName),
                        completedEq(completed),
                        todo.user.username.eq(username)
                )
                // 페이징 & 정렬 추가
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(todo.createdAt.desc())
                .fetch();

        // 전체 개수 조회 (Count) - 페이징을 위해 필요함
        Long total = queryFactory
                .select(todo.count())
                .from(todo)
                .leftJoin(todo.tags, tag)
                .where(
                        titleContains(title),
                        tagEq(tagName),
                        completedEq(completed),
                        todo.user.username.eq(username)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    private BooleanExpression titleContains(String title) {
        return title != null ? todo.title.contains(title) : null;
    }

    private BooleanExpression tagEq(String tagName) {
        return tagName != null ? tag.name.eq(tagName) : null;
    }

    private BooleanExpression completedEq(Boolean completed) {
        return completed != null ? todo.completed.eq(completed) : null;
    }

    private BooleanExpression usernameEq(String username) {
        return todo.user.username.eq(username);
    }
}
