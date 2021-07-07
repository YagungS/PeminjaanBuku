package com.kra.peminjaman.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.kra.peminjaman.model.Buku;
import com.kra.peminjaman.model.Mahasiswa;
import com.kra.peminjaman.repository.BukuRepository;
import com.kra.peminjaman.repository.MahasiswaRepository;
import com.kra.peminjaman.service.PeminjamanService;

@RestController
@RequestMapping("/api")
public class PeminjamanController {

    private final MahasiswaRepository mhsRepository;
    private final BukuRepository bukuRepository;
    
    @Autowired
    private PeminjamanService peminjamanService;

    @Autowired
    public PeminjamanController(MahasiswaRepository mhsRepository, BukuRepository bukuRepository) {
        this.mhsRepository = mhsRepository;
        this.bukuRepository = bukuRepository;
    }

    @PostMapping("/mahasiswa")
    public ResponseEntity<Mahasiswa> create(@Valid @RequestBody Mahasiswa mhs) {
        if(!peminjamanService.isExistsMahasiswa(mhs)) {
        	Mahasiswa maba = mhsRepository.save(mhs);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(maba.getId()).toUri();
            return ResponseEntity.created(location).body(maba);
        }       
        throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Mahasiswa (%s) sudah ada.", mhs.getName()));
    }

    @PostMapping("/buku")
    public ResponseEntity<Buku> create(@Valid @RequestBody Buku book) {
        if(!peminjamanService.isExistsBuku(book)) {
        	Buku newBook = bukuRepository.save(book);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newBook.getId()).toUri();
            return ResponseEntity.created(location).body(newBook);
        }       
        throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Buku (%s) sudah ada.", book.getName()));
    }
        
    @PutMapping("/pinjam/{idMhs}/{idBuku}")
    public ResponseEntity<Mahasiswa> pinjam(@PathVariable Integer idMhs,@PathVariable Integer idBuku) {
    	Optional<Mahasiswa> mhs = mhsRepository.findById(idMhs);
    	if(!mhs.isPresent()) {
    		throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Mahasiswa id (%s) tidak ditemukan", idMhs));
    	}
    	
    	Optional<Buku> book = bukuRepository.findById(idBuku);    	
    	if(!book.isPresent()) {
    		throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Buku id (%s) tidak ditemukan", idBuku));
    	}
    	
    	if(!peminjamanService.isBukuFree(book.get())) {
    		throw new ResponseStatusException(HttpStatus.IM_USED, String.format("Buku id (%s) sedang dipinjam.", idBuku));
    	}
    	
    	Mahasiswa updatedmhs = peminjamanService.pinjamBuku(mhs.get(), book.get());
    	if(updatedmhs == null)
    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Internal server error"));
    	return ResponseEntity.status(HttpStatus.OK).body(updatedmhs);

    }
    
    @PutMapping("/kembali/{idMhs}/{idBuku}")
    public ResponseEntity<Mahasiswa> kembali(@PathVariable Integer idMhs,@PathVariable Integer idBuku) {
    	Optional<Mahasiswa> mhs = mhsRepository.findById(idMhs);
    	if(!mhs.isPresent()) {
    		throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Mahasiswa id (%s) tidak ditemukan", idMhs));
    	}
    	
    	Optional<Buku> book = bukuRepository.findById(idBuku);    	
    	if(!book.isPresent()) {
    		throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Buku id (%s) tidak ditemukan", idBuku));
    	}
    	
    	if(peminjamanService.isBukuFree(book.get())) {
    		throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Buku id (%s) sedang tidak dipinjam.", idBuku));
    	}
    	
    	if(!peminjamanService.isOnPinjamanList(mhs.get(),book.get())) {
    		throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Buku id (%s) tidak ada dalam pinjaman Mahasiswa (%s).", idBuku,idMhs));
    	}
    	
    	Mahasiswa updatedmhs = peminjamanService.pengembalianBuku(mhs.get(), book.get());
    	if(updatedmhs == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Internal server error"));
        }
    	return ResponseEntity.status(HttpStatus.OK).body(updatedmhs);
    }

    //buat check data
    @GetMapping("/mahasiswa/{id}")
    public ResponseEntity<Mahasiswa> getMhsById(@PathVariable Integer id) {
        Optional<Mahasiswa> mhs = mhsRepository.findById(id);
        if (!mhs.isPresent()) {
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.ok(mhs.get());
    }
    
    @GetMapping("/mahasiswa/all")
    public ResponseEntity<List<Mahasiswa>> getAllMhs() {
        return ResponseEntity.ok(peminjamanService.getAllMahasiswa());
    }
    
    @GetMapping("/buku/{id}")
    public ResponseEntity<Buku> getBookById(@PathVariable Integer id) {
        Optional<Buku> book = bukuRepository.findById(id);
        if (!book.isPresent()) {
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.ok(book.get());
    }
    
    @GetMapping("/buku/all")
    public ResponseEntity<List<Buku>> getAllBook() {
        return ResponseEntity.ok(peminjamanService.getAllBuku());
    }
}