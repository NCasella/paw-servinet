package ar.edu.itba.paw.model;

import javax.persistence.*;

@MappedSuperclass
public class BasicService {

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "services_id_seq")
        @SequenceGenerator(name = "services_id_seq", sequenceName = "services_id_seq", allocationSize = 1)
        @Column(name = "id")
        private Long id;

        @Column(name = "businessid", nullable = false)
        private long businessid;

        @Column(name = "servicename", nullable = false, length = 255)
        private String name;

        @Column(name = "location", nullable = false, length = 255)
        private String location;

        @Column(name = "imageId")
        private Long imageId;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "businessid", insertable = false, updatable = false)
        private Business business;


        public BasicService() {
        }

        public BasicService(long businessid, String name, String location,long imageId) {
            this.businessid = businessid;
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

        public void setBusinessid(long businessid) {
            this.businessid = businessid;
        }

        public long getBusinessid() {
            return businessid;
        }

        public long getImageId() {
        return imageId != null ? imageId : 0;
    }

        public void setImageId(long imageId) {
            this.imageId = imageId;
        }

        public Business getBusiness(){
            return business;
        }

}
