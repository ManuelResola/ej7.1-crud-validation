package com.bosonit.formacion.ej7.crudvalidation.person.application;

import com.bosonit.formacion.ej7.crudvalidation.exceptions.EntityNotFoundException;
import com.bosonit.formacion.ej7.crudvalidation.exceptions.UnprocessableEntityException;
import com.bosonit.formacion.ej7.crudvalidation.person.domain.Person;
import com.bosonit.formacion.ej7.crudvalidation.person.infraestructure.controller.input.PersonInputDto;
import com.bosonit.formacion.ej7.crudvalidation.person.infraestructure.controller.output.PersonOutputDto;
import com.bosonit.formacion.ej7.crudvalidation.person.infraestructure.controller.output.PersonOutputDtoWithRoleDetails;
import com.bosonit.formacion.ej7.crudvalidation.person.infraestructure.repository.PersonRepository;
import com.bosonit.formacion.ej7.crudvalidation.student.domain.Student;
import com.bosonit.formacion.ej7.crudvalidation.student.infrastructure.repository.StudentRepository;
import com.bosonit.formacion.ej7.crudvalidation.student_subject.domain.StudentSubject;
import com.bosonit.formacion.ej7.crudvalidation.student_subject.infrastructure.repository.StudentSubjectRepository;
import com.bosonit.formacion.ej7.crudvalidation.teacher.domain.Teacher;
import com.bosonit.formacion.ej7.crudvalidation.teacher.infrastructure.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonServiceImp implements PersonService{

    @Autowired
    PersonRepository personRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    StudentSubjectRepository studentSubjectRepository;

    @Override
    public PersonOutputDto addPerson(PersonInputDto newPersonDto) throws Exception {
        newPersonDto.setCreated_date(new Date());

        if(newPersonDto.getUser() == null)
            throw new UnprocessableEntityException("User no puede ser null", 422);
        if(newPersonDto.getUser().length() > 10 || newPersonDto.getUser().length() < 6)
            throw new UnprocessableEntityException("User debe tener entre 6 y 10 caracteres", 422);
        if(newPersonDto.getPassword() == null)
            throw new UnprocessableEntityException("Password no puede ser null", 422);
        if(newPersonDto.getName() == null)
            throw new UnprocessableEntityException("Name no puede ser null", 422);
        if(newPersonDto.getCompany_email() == null)
            throw new UnprocessableEntityException("Company email no puede ser null", 422);
        if(!newPersonDto.getCompany_email().contains("@"))
            throw new UnprocessableEntityException("Email no tiene el formato correcto", 422);
        if(newPersonDto.getPersonal_email() == null)
            throw new UnprocessableEntityException("Company email no puede ser null", 422);
       if(!newPersonDto.getPersonal_email().contains("@"))
            throw new UnprocessableEntityException("Email no tiene el formato correcto", 422);
       if(newPersonDto.getCity() == null)
            throw new UnprocessableEntityException("City no puede ser null", 422);
       if(newPersonDto.getCreated_date() == null)
            throw new UnprocessableEntityException("Created date no puede ser null", 422);

       Person newPerson = newPersonDto.transformIntoPerson();

       personRepository.save(newPerson);

       return new PersonOutputDto(newPerson);
    }

    @Override
    public PersonOutputDtoWithRoleDetails findPersonById(int id) throws Exception {
        Optional<Person> personOptional = personRepository.findById(id);

        if(personOptional.isEmpty())
            throw new EntityNotFoundException("Person no existe", 404);

        Optional<Teacher> teacherOpt = teacherRepository.findByPerson(personOptional.get());
        Optional<Student> studentOpt = studentRepository.findByPerson(personOptional.get());
        List<Student> studentList = new ArrayList<>();

        if(teacherOpt.isPresent()){
            List<StudentSubject> subjectsList = studentSubjectRepository.findByTeacher(teacherOpt.get());

            for(StudentSubject subject : subjectsList)
                for(Student student : subject.getStudents())
                    if(!studentList.contains(student))
                        studentList.add(student);
        }

        return new PersonOutputDtoWithRoleDetails(personOptional.get(), teacherOpt.orElse(null), studentList, studentOpt.orElse(null));
    }

    @Override
    public List<PersonOutputDto> findPersonByUsername(String username) throws Exception {
       List<Person> peopleList = personRepository.findByUsername(username);

       if(peopleList.isEmpty())
           throw new EntityNotFoundException("Persona no existe", 404);


       return peopleList.stream().map(person -> new PersonOutputDto(person)).collect(Collectors.toList());

    }

    @Override
    public List<PersonOutputDto> getAllPeople() {
        List<Person> peopleList = personRepository.findAll();

        List<PersonOutputDto> peopleOutputDto = new ArrayList<>();


        peopleOutputDto = peopleList.stream().map(person -> new PersonOutputDto(person)).collect(Collectors.toList());

        return peopleOutputDto;
    }

    @Override
    public PersonOutputDto updatePerson(int id, PersonInputDto personInputDto) throws Exception {

        Optional<Person> personOpt = personRepository.findById(id);

        if(personOpt.isEmpty())
            throw new EntityNotFoundException("Person no existe", 404);
        if(personInputDto.getUser() == null)
            throw new UnprocessableEntityException("User no puede ser null", 422);
        if(personInputDto.getUser().length() > 10 || personInputDto.getUser().length() < 6)
            throw new UnprocessableEntityException("User debe tener entre 6 y 10 caracteres", 422);
        if(personInputDto.getPassword() == null)
            throw new UnprocessableEntityException("Password no puede ser null", 422);
        if(personInputDto.getName() == null)
            throw new UnprocessableEntityException("Name no puede ser null", 422);
        if(personInputDto.getCompany_email() == null)
            throw new UnprocessableEntityException("Company email no puede ser null", 422);
        if(!personInputDto.getCompany_email().contains("@"))
            throw new UnprocessableEntityException("Email no tiene el formato correcto", 422);
        if(personInputDto.getPersonal_email() == null)
            throw new UnprocessableEntityException("Company email no puede ser null", 422);
        if(!personInputDto.getPersonal_email().contains("@"))
            throw new UnprocessableEntityException("Email no tiene el formato correcto", 422);
        if(personInputDto.getCity() == null)
            throw new UnprocessableEntityException("City no puede ser null", 422);

        Person person = personOpt.get();

        person.setUsername(personInputDto.getUser());
        person.setPassword(personInputDto.getPassword());
        person.setName(personInputDto.getName());
        person.setSurname(personInputDto.getSurname());
        person.setCompany_email(personInputDto.getCompany_email());
        person.setPersonal_email(personInputDto.getCompany_email());
        person.setCity(personInputDto.getCity());
        person.setActive(personInputDto.getActive());
        person.setImagen_url(personInputDto.getImagen_url());

        personRepository.save(person);

        return new PersonOutputDto(person);
    }

    @Override
    public String deletePerson(int id) throws Exception {

        Optional<Person> personOpt = personRepository.findById(id);

        if(personOpt.isEmpty())
            throw new EntityNotFoundException("La persona no existe", 404);

        Optional<Student> studentOptional = studentRepository.findByPerson(personOpt.get());
        Optional<Teacher> teacherOptional = teacherRepository.findByPerson(personOpt.get());

        if(teacherOptional.isPresent())
            throw new UnprocessableEntityException("La persona es profesor", 422);
        if(studentOptional.isPresent())
            throw new UnprocessableEntityException("La persona es estudiante", 422);

        personRepository.delete(personOpt.get());

        return "La persona ha sido borrada con éxito";
    }

    @Override
    public Optional<Person> getPersonOptional(int id) {
        return personRepository.findById(id);
    }
}
