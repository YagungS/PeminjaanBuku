package com.kra.peminjaman.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kra.peminjaman.model.Buku;

public interface BukuRepository extends JpaRepository<Buku, Integer>{
}