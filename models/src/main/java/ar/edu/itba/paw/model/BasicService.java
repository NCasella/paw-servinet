package ar.edu.itba.paw.model;

import javax.persistence.*;

@MappedSuperclass
public class BasicService {

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "services_id_seq")
        @SequenceGenerator(name = "services_id_seq", sequenceName = "services_id_seq", allocationSize = 1)
        @Column(name = "id")
        private Long id;


        @Column(name = "servicename", nullable = false)
        private String name;

        @Column(name = "location", nullable = false)
        private String location;

        @Column(name = "imageId")
        private Long imageId;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "businessid", updatable = false)
        private Business business;


        public BasicService() {
        }

        public BasicService(Business business, String name, String location,Long imageId) {
            this.business= business;
            this.name = name;
            this.location = location;
            this.imageId= imageId;
        }

        // Getters and setters
        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public void setBusiness(Business business) {
            this.business = business;
        }

        public long getBusinessid() {
            return business.getBusinessid();
        }

        public Long getImageId() {
            return imageId!=null ?imageId:-1;
        }

        public void setImageId(long imageId) {
            this.imageId = imageId;
        }

        public Business getBusiness(){
            return business;
        }

}
