package com.kra.peminjaman.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kra.peminjaman.model.Mahasiswa;

public interface MahasiswaRepository extends JpaRepository<Mahasiswa, Integer>{
}
