package com.aabello.iwalk.model.db;

import com.aabello.iwalk.model.types.GenderType;
import com.aabello.iwalk.model.types.GenderTypeConverter;
import com.aabello.iwalk.model.types.HeightUnitType;
import com.aabello.iwalk.model.types.HeightUnitTypeConverter;
import com.aabello.iwalk.model.types.WeightUnitType;
import com.aabello.iwalk.model.types.WeightUnitTypeConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.DaoException;

@Entity
public class User {

    @Id(autoincrement = true)
    private Long id;

    private int age;

    @Convert(converter = GenderTypeConverter.class, columnType = String.class)
    private GenderType genderType;

    @Convert(converter = HeightUnitTypeConverter.class, columnType = String.class)
    private HeightUnitType heightUnitType;

    private double height;

    @Convert(converter = WeightUnitTypeConverter.class, columnType = String.class)
    private WeightUnitType weightUnitType;

    private double weight;


    private String smokingHabit;
    private int target;

    @ToMany(referencedJoinProperty = "userId")
    private List<WalkTest> walkTests;

    @ToMany (referencedJoinProperty = "userId")
    private List<DailyActivity> dailyActivities;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1507654846)
    private transient UserDao myDao;

    @Generated(hash = 586692638)
    public User() {
    }

    @Generated(hash = 1859164408)
    public User(Long id, int age, GenderType genderType, HeightUnitType heightUnitType, double height, WeightUnitType weightUnitType, double weight, String smokingHabit,
            int target) {
        this.id = id;
        this.age = age;
        this.genderType = genderType;
        this.heightUnitType = heightUnitType;
        this.height = height;
        this.weightUnitType = weightUnitType;
        this.weight = weight;
        this.smokingHabit = smokingHabit;
        this.target = target;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSmokingHabit() {
        return smokingHabit;
    }

    public void setSmokingHabit(String smokingHabit) {
        this.smokingHabit = smokingHabit;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public GenderType getGenderType() {
        return this.genderType;
    }

    public void setGenderType(GenderType genderType) {
        this.genderType = genderType;
    }

    public HeightUnitType getHeightUnitType() {
        return this.heightUnitType;
    }

    public void setHeightUnitType(HeightUnitType heightUnitType) {
        this.heightUnitType = heightUnitType;
    }

    public WeightUnitType getWeightUnitType() {
        return this.weightUnitType;
    }

    public void setWeightUnitType(WeightUnitType weightUnitType) {
        this.weightUnitType = weightUnitType;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1332482691)
    public List<WalkTest> getWalkTests() {
        if (walkTests == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            WalkTestDao targetDao = daoSession.getWalkTestDao();
            List<WalkTest> walkTestsNew = targetDao._queryUser_WalkTests(id);
            synchronized (this) {
                if (walkTests == null) {
                    walkTests = walkTestsNew;
                }
            }
        }
        return walkTests;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1665078708)
    public synchronized void resetWalkTests() {
        walkTests = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1325437623)
    public List<DailyActivity> getDailyActivities() {
        if (dailyActivities == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DailyActivityDao targetDao = daoSession.getDailyActivityDao();
            List<DailyActivity> dailyActivitiesNew = targetDao._queryUser_DailyActivities(id);
            synchronized (this) {
                if (dailyActivities == null) {
                    dailyActivities = dailyActivitiesNew;
                }
            }
        }
        return dailyActivities;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1458205722)
    public synchronized void resetDailyActivities() {
        dailyActivities = null;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2059241980)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserDao() : null;
    }

}
