package com.solux.sm137.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(nullable = false)
    private String categoryName;

    @Column(nullable = false)
    private Integer level;

    @Column(nullable = false)
    private Integer parentId;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Complaint> complaints;
}
