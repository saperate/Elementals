package dev.saperate.elementals.elements;

import dev.saperate.elementals.Constants;
import dev.saperate.elementals.data.Bender;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class Element{
    private static final HashMap<String,Element> elements = new HashMap<>();
    private final List<Ability> abilityList = new ArrayList<>();
    public final List<Ability> bindableAbilities = new ArrayList<>();
    private final Map<String, Integer> upgradeKeybinds = new HashMap<>();
    public final String name;
    public Upgrade root;

    public Element(String name, Upgrade root){
        if(elements.containsKey(name.toLowerCase(Locale.CANADA))){
            throw new RuntimeException("Element \""+name+"\" was already registered!");
        }
        elements.put(name.toLowerCase(Locale.CANADA),this);
        this.name = name;
        this.root = root;
    }

    public Element(String name, Upgrade[] upgrades){
        this(name,new Upgrade(name,upgrades,0));
    }

    public Element(String name){
        this(name,new Upgrade[0]);
    }

    public void addAbility(Ability a, boolean bindable){
        if(!abilityList.contains(a)){
            abilityList.add(a);
            if(bindable){
                bindableAbilities.add(a);
            }
        }
    }

    public void addAbility(Ability a){
        addAbility(a,false);
    }

    /**
     * Adds an ability to this element and binds it to an explicit keybind slot (0-9, matching
     * KeyAbility1-10), regardless of the order in which addAbility() calls happen to occur.
     * <br>Use this instead of {@link #addAbility(Ability, boolean)} whenever an ability needs to sit
     * on a specific key (for example, a bifurcated/exclusive upgrade branch that got its own dedicated
     * keybind) without disturbing the bindable slot of abilities added before or after it.
     *
     * @param a            The ability being registered
     * @param bindableSlot The explicit bindable slot (0-9) this ability should occupy
     */
    public void addAbility(Ability a, int bindableSlot) {
        if (!abilityList.contains(a)) {
            abilityList.add(a);
            while (bindableAbilities.size() <= bindableSlot) {
                bindableAbilities.add(null);
            }
            bindableAbilities.set(bindableSlot, a);
        }
    }

    /**
     * Registers which bindable slot (0-9) governs a given upgrade branch, so the skill tree GUI can
     * show the correct key in "Use: %d" tooltips. Every promoted/bifurcated branch (an upgrade that
     * got its own dedicated keybind instead of being reached through a sneak/hold combo on a base key)
     * should call this once with its own upgrade name.
     *
     * @param upgradeName  The name of the upgrade node that gates this ability (the string passed to canUseUpgrade)
     * @param bindableSlot The bindable slot (0-9) this upgrade's ability is bound to
     */
    public void registerUpgradeKeybind(String upgradeName, int bindableSlot) {
        upgradeKeybinds.put(upgradeName, bindableSlot);
    }

    /**
     * Resolves which bindable slot (0-9) should be shown in the GUI for a given upgrade node,
     * walking up the tree until it finds an ancestor (or itself) that was explicitly registered via
     * {@link #registerUpgradeKeybind(String, int)}. Falls back to the index of the upgrade's top-level
     * root branch (the historical behaviour) if nothing more specific was registered.
     *
     * @param upgrade The upgrade node currently hovered/displayed in the GUI
     * @return The bindable slot index whose keybind should be displayed for this upgrade
     */
    public int getKeybindSlotForUpgrade(Upgrade upgrade) {
        Upgrade curr = upgrade;
        while (curr != null) {
            Integer slot = upgradeKeybinds.get(curr.name);
            if (slot != null) {
                return slot;
            }
            curr = curr.parent;
        }

        Upgrade head = upgrade.getHead();
        for (int i = 0; i < root.children.length; i++) {
            if (root.children[i].equals(head)) {
                return i;
            }
        }
        return 0;
    }

    public Ability getBindableAbility(int index){
        if(index == -1 || index >= bindableAbilities.size()){
            return null;
        }
        return bindableAbilities.get(index);
    }

    public Ability getAbility(int index){
        return abilityList.get(index);
    }

    public int getIndexOfAbility(Ability obj){
        return abilityList.indexOf(obj);
    }

    public boolean contains(Ability ability){
        return abilityList.contains(ability);
    }

    /**
     * Gets an element using its name. If it is not found, returns a reference to {@link NoneElement}
     */
    public static Element getElement(String name){
        return elements.getOrDefault(name.toLowerCase(Locale.CANADA),elements.get("None"));
    }


    public String getName() {
        return name;
    }

    public CompoundTag onSave(HashMap<Upgrade,Boolean> plrUpgrades){
        CompoundTag nbt = new CompoundTag();
        for(Upgrade child : root.children){
            child.onSave(nbt,plrUpgrades);
        }
        return nbt;
    }

    public void onRead(CompoundTag nbt, HashMap<Upgrade,Boolean> plrUpgrades){
        for(Upgrade child : root.children){
            child.onRead(nbt,plrUpgrades);
        }
    }

    /**
     * Returns a list of all the background textures for this specific element. Will only be used on the skill
     * tree. The index of which each is placed determines what order it is placed on.
     * 0 being the bottom (rendered first), n being the top (rendered last).
     * When used in the skill tree, we will search Identifier.Of(MODID, "textures/gui/background/[this.name]/texName"
     */
    public String[] getBackgroundTextures(){
        return new String[]{};
    }

    public abstract boolean isSkillTreeComplete(Bender bender);

    public int getColor(){
        return 0xFFa0e8e6;
    }

    public int getSecondaryColor(){
        return 0xFF13AEA9;
    }

    public ResourceLocation getGuiBackgroundIdentifier(){
        return ResourceLocation.fromNamespaceAndPath(Constants.MODID,"textures/gui/default_gui_background.png");
    }

    public int getTertiaryColor(){
        return 0xFFffef00;
    }

    @Nullable
    public ResourceLocation getOverlayTexture(){
        return null;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    public static List<Element> getElementList(){
        return elements.values().stream().toList();
    }

}