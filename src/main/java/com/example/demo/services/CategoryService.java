package com.example.demo.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.DTO.CategoryDTO;
import com.example.demo.entitties.Category;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {

  @Autowired
  private CategoryRepository repository;

  @Transactional(readOnly = true)
  public List<CategoryDTO> findAll() {
    List<Category> list = repository.findAll();
    return list.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public CategoryDTO findById(Long id) {
    Optional<Category> obj = repository.findById(id);
    Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found."));
    return new CategoryDTO(entity);
  }

  @Transactional
  public CategoryDTO insert(CategoryDTO DTO) {
    Category entity = new Category();
    entity.setName(DTO.getName());
    entity = repository.save(entity);
    return new CategoryDTO(entity);
  }

  @Transactional
  public CategoryDTO update(Long id, CategoryDTO DTO) {
    try {
      Category entity = repository.getReferenceById(id);
      entity.setName(DTO.getName());
      entity = repository.save(entity);
      return new CategoryDTO(entity);
    }

    catch (EntityNotFoundException e) {
      throw new ResourceNotFoundException("Id " + id + " not found ");
    }

  }
}

// List<CategoryDTO> listDTO = new ArrayList();
// for (Category cat: list) {
// listDTO.add(new CategoryDTO(cat));
// }
// return listDTO;
// }