package org.fireflow.model.resource;

public class AbstractResource implements IResource {

	/**
	 * 资源的名称
	 */
    private String name;
    
    /**
     * 资源的显示名称
     */
    private String displayName;
    
    /**
     * 资源的描述
     */
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String label) {
        this.displayName = label;
    }

    public String toString() {
        if (this.displayName != null && !this.displayName.trim().equals("")) {
            return this.displayName;
        } else {
            return this.name;
        }
    }
}
